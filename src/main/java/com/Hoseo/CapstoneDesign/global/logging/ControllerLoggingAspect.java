package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import com.Hoseo.CapstoneDesign.global.logging.dto.ErrorInfo;
import com.Hoseo.CapstoneDesign.global.logging.support.HttpEventType;
import com.Hoseo.CapstoneDesign.global.logging.support.LogCategory;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.ERROR_ALREADY_LOGGED;

@Aspect
@Component
public class ControllerLoggingAspect {

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password",
            "passwd",
            "secret",
            "token",
            "authorization",
            "cookie",
            "signature",
            "state",
            "credential"
    );

    private final StructuredHttpLogger structuredHttpLogger;

    public ControllerLoggingAspect(StructuredHttpLogger structuredHttpLogger) {
        this.structuredHttpLogger = structuredHttpLogger;
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logHttp(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = currentRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = method.getName();
        Map<String, Object> args = extractArgs(signature.getParameterNames(), joinPoint.getArgs());

        structuredHttpLogger.info(
                LogCategory.HTTP.name(),
                HttpEventType.HTTP_REQUEST.name(),
                className,
                methodName,
                "Incoming request",
                args,
                request,
                null,
                null
        );

        try {
            Object result = joinPoint.proceed();

            int successStatus = resolveSuccessStatus(method, result);

            structuredHttpLogger.info(
                    LogCategory.HTTP.name(),
                    HttpEventType.HTTP_RESPONSE.name(),
                    className,
                    methodName,
                    resolveSuccessMessage(result),
                    null,
                    request,
                    successStatus,
                    structuredHttpLogger.resolveDurationMs(request)
            );

            return result;
        } catch (Throwable ex) {
            ErrorInfo errorInfo = resolveErrorInfo(ex);

            request.setAttribute(ERROR_ALREADY_LOGGED, true);

            structuredHttpLogger.error(
                    LogCategory.HTTP.name(),
                    HttpEventType.HTTP_ERROR.name(),
                    className,
                    methodName,
                    "Request failed",
                    null,
                    request,
                    errorInfo.httpStatus(),
                    structuredHttpLogger.resolveDurationMs(request),
                    errorInfo
            );

            throw ex;
        }
    }

    private ServletRequestAttributes currentRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes;
        }
        return null;
    }

    private Map<String, Object> extractArgs(String[] parameterNames, Object[] args) {
        Map<String, Object> result = new LinkedHashMap<>();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (shouldSkip(arg)) {
                continue;
            }

            String parameterName = parameterNames != null && parameterNames.length > i
                    ? parameterNames[i]
                    : "arg" + i;

            result.put(parameterName, toSafeValue(parameterName, arg));
        }

        return result.isEmpty() ? null : result;
    }

    private boolean shouldSkip(Object arg) {
        return arg == null
                || arg instanceof ServletRequest
                || arg instanceof ServletResponse
                || arg instanceof BindingResult
                || arg instanceof Principal
                || arg instanceof MultipartFile
                || arg instanceof MultipartFile[];
    }

    private Object toSafeValue(String fieldName, Object value) {
        if (isSensitiveKey(fieldName)) {
            return "<redacted>";
        }
        return toSafeValue(value);
    }

    private Object toSafeValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof UUID
                || value.getClass().isEnum()) {
            return value;
        }

        if (value instanceof Collection<?> collection) {
            return collection.stream().map(this::toSafeValue).toList();
        }

        if (value instanceof Map<?, ?> map) {
            Map<String, Object> safeMap = new LinkedHashMap<>();
            map.forEach((k, v) -> safeMap.put(String.valueOf(k), toSafeValue(String.valueOf(k), v)));
            return safeMap;
        }

        return "<omitted:" + value.getClass().getSimpleName() + ">";
    }

    private boolean isSensitiveKey(String fieldName) {
        String normalized = fieldName == null ? "" : fieldName.toLowerCase(Locale.ROOT);
        return SENSITIVE_KEYS.stream().anyMatch(normalized::contains);
    }

    private int resolveSuccessStatus(Method method, Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getStatusCode().value();
        }

        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(method, ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value().value();
        }

        return 200;
    }

    private String resolveSuccessMessage(Object result) {
        if (result instanceof ResponseBodyEmitter) {
            return "Streaming response started";
        }
        return "Request completed";
    }

    private ErrorInfo resolveErrorInfo(Throwable ex) {
        if (ex instanceof GlobalBaseException globalBaseException) {
            GlobalErrorCode errorCode = globalBaseException.getErrorCode();
            return new ErrorInfo(
                    errorCode.getMessage(),
                    errorCode.getErrorCode(),
                    errorCode.getHttpStatus().value()
            );
        }

        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            return new ErrorInfo(
                    ex.getMessage(),
                    responseStatus.value().value(),
                    responseStatus.value().value()
            );
        }

        // CustomException 분기 필요시 추가
        // 예:
        // if (ex instanceof ProjectException e) {
        //     return new ErrorInfo(e.getMessage(), e.getErrorCode().name(), e.getErrorCode().getHttpStatus().value());
        // }

        return new ErrorInfo(
                ex.getMessage(),
                500,
                500
        );
    }
}
