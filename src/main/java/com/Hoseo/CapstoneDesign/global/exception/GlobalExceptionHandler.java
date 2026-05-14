package com.Hoseo.CapstoneDesign.global.exception;

import com.Hoseo.CapstoneDesign.auth.exception.AuthErrorCode;
import com.Hoseo.CapstoneDesign.global.logging.StructuredHttpLogger;
import com.Hoseo.CapstoneDesign.global.logging.dto.ErrorInfo;
import com.Hoseo.CapstoneDesign.global.logging.support.HttpEventType;
import com.Hoseo.CapstoneDesign.global.logging.support.LogCategory;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.ERROR_ALREADY_LOGGED;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.HANDLER_CLASS_NAME;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.HANDLER_METHOD_NAME;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final StructuredHttpLogger structuredHttpLogger;

    public GlobalExceptionHandler(ObjectProvider<StructuredHttpLogger> structuredHttpLoggerProvider) {
        this.structuredHttpLogger = structuredHttpLoggerProvider.getIfAvailable();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        if (canWriteStructuredLog(request)) {
            request.setAttribute(ERROR_ALREADY_LOGGED, true);
            structuredHttpLogger.error(
                    LogCategory.HTTP.name(),
                    HttpEventType.HTTP_ERROR.name(),
                    getClassName(request),
                    getMethodName(request),
                    "Validation failed",
                    Map.of("fieldErrors", errors),
                    request,
                    HttpStatus.BAD_REQUEST.value(),
                    structuredHttpLogger.resolveDurationMs(request),
                    new ErrorInfo(
                            "Validation failed",
                            HttpStatus.BAD_REQUEST.value(),
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(GlobalBaseException.class)
    public ResponseEntity<GlobalExceptionResponse> handleGlobalBaseException(
            GlobalBaseException exception,
            HttpServletRequest request
    ) {
        GlobalErrorCode errorCode = exception.getErrorCode();
        GlobalExceptionResponse response = new GlobalExceptionResponse(errorCode);

        if (canWriteStructuredLog(request)) {
            request.setAttribute(ERROR_ALREADY_LOGGED, true);
            structuredHttpLogger.error(
                    LogCategory.HTTP.name(),
                    HttpEventType.HTTP_ERROR.name(),
                    getClassName(request),
                    getMethodName(request),
                    "Business exception occurred",
                    null,
                    request,
                    errorCode.getHttpStatus().value(),
                    structuredHttpLogger.resolveDurationMs(request),
                    structuredHttpLogger.toErrorInfo(errorCode)
            );
        }

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<GlobalExceptionResponse> handleMissingRequestCookie(
            MissingRequestCookieException exception,
            HttpServletRequest request
    ) {
        AuthErrorCode errorCode = AuthErrorCode.UNAUTHORIZED;
        GlobalExceptionResponse response = new GlobalExceptionResponse(errorCode);

        if (canWriteStructuredLog(request)) {
            request.setAttribute(ERROR_ALREADY_LOGGED, true);
            structuredHttpLogger.error(
                    LogCategory.HTTP.name(),
                    HttpEventType.HTTP_ERROR.name(),
                    getClassName(request),
                    getMethodName(request),
                    "Required request cookie is missing",
                    Map.of("cookieName", exception.getCookieName()),
                    request,
                    errorCode.getHttpStatus().value(),
                    structuredHttpLogger.resolveDurationMs(request),
                    structuredHttpLogger.toErrorInfo(errorCode)
            );
        }

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(
            NoResourceFoundException e,
            HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", "요청한 리소스를 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(JwtUtilException.class)
    public ResponseEntity<GlobalExceptionResponse> handleJwtUtilException(
            JwtUtilException exception,
            HttpServletRequest request
    ) {
        var errorCode = exception.getErrorCode();
        GlobalExceptionResponse response = new GlobalExceptionResponse(errorCode);

        if (canWriteStructuredLog(request)) {
            request.setAttribute(ERROR_ALREADY_LOGGED, true);
            structuredHttpLogger.error(
                    LogCategory.HTTP.name(),
                    HttpEventType.HTTP_ERROR.name(),
                    getClassName(request),
                    getMethodName(request),
                    "JWT exception occurred",
                    null,
                    request,
                    errorCode.getHttpStatus().value(),
                    structuredHttpLogger.resolveDurationMs(request),
                    structuredHttpLogger.toErrorInfo(errorCode)
            );
        }

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        if (canWriteStructuredLog(request)) {
            request.setAttribute(ERROR_ALREADY_LOGGED, true);
            structuredHttpLogger.error(
                    LogCategory.HTTP.name(),
                    HttpEventType.HTTP_ERROR.name(),
                    getClassName(request),
                    getMethodName(request),
                    "Unhandled server error",
                    null,
                    request,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    structuredHttpLogger.resolveDurationMs(request),
                    new ErrorInfo(
                            "서버 오류입니다.",
                            500,
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
            );
        }

        log.error("[GLOBAL_EXCEPTION] 500 ERROR -> {}", e.getMessage(), e);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "서버 오류입니다.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    private String getClassName(HttpServletRequest request) {
        if (structuredHttpLogger == null) {
            Object value = request.getAttribute(HANDLER_CLASS_NAME);
            return value != null ? value.toString() : "UnknownHandler";
        }
        return structuredHttpLogger.resolveHandlerClassName(request, "UnknownHandler");
    }

    private String getMethodName(HttpServletRequest request) {
        if (structuredHttpLogger == null) {
            Object value = request.getAttribute(HANDLER_METHOD_NAME);
            return value != null ? value.toString() : "unknown";
        }
        return structuredHttpLogger.resolveHandlerMethodName(request, "unknown");
    }

    private boolean canWriteStructuredLog(HttpServletRequest request) {
        return structuredHttpLogger != null && !Boolean.TRUE.equals(request.getAttribute(ERROR_ALREADY_LOGGED));
    }
}
