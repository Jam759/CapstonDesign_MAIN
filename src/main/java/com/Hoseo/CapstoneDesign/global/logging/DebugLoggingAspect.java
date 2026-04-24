package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.global.logging.dto.AppErrorInfo;
import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import com.Hoseo.CapstoneDesign.global.logging.support.AppEventType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
@ConditionalOnProperty(prefix = "app.logging.debug-aspect", name = "enabled", havingValue = "true")
public class DebugLoggingAspect {

    private static final Set<String> OBJECT_METHOD_NAMES = Set.of("toString", "hashCode", "equals");

    private final LoggingProperties loggingProperties;
    private final DebugLogSanitizer sanitizer;
    private final StructuredAppLogger structuredAppLogger;

    public DebugLoggingAspect(LoggingProperties loggingProperties, StructuredAppLogger structuredAppLogger) {
        this.loggingProperties = loggingProperties;
        this.sanitizer = new DebugLogSanitizer(loggingProperties.getDebugAspect());
        this.structuredAppLogger = structuredAppLogger;
    }

    @Around("""
            execution(* com.Hoseo.CapstoneDesign..service..*(..)) ||
            execution(* com.Hoseo.CapstoneDesign..repository..*(..)) ||
            execution(* com.Hoseo.CapstoneDesign..mapper..*(..)) ||
            within(@org.springframework.stereotype.Service *) ||
            within(@org.springframework.stereotype.Repository *) ||
            within(@org.apache.ibatis.annotations.Mapper *) ||
            within(@com.Hoseo.CapstoneDesign.global.annotation.Facade *)
            """)
    public Object logServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (method.getDeclaringClass() == Object.class || OBJECT_METHOD_NAMES.contains(method.getName())) {
            return joinPoint.proceed();
        }

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = method.getName();
        Map<String, Object> args = sanitizer.sanitizeArgs(signature.getParameterNames(), joinPoint.getArgs());

        structuredAppLogger.trace(
                AppEventType.METHOD_ENTER.name(),
                className,
                methodName,
                "Method entered",
                args,
                null,
                null,
                null
        );

        long startNanos = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;

            Object sanitizedResult = loggingProperties.getDebugAspect().isLogReturnValue()
                    ? sanitizer.sanitizeReturnValue(result)
                    : null;

            structuredAppLogger.trace(
                    AppEventType.METHOD_EXIT.name(),
                    className,
                    methodName,
                    "Method completed",
                    null,
                    durationMs,
                    sanitizedResult,
                    null
            );

            return result;
        } catch (Throwable ex) {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;

            structuredAppLogger.error(
                    AppEventType.METHOD_THROW.name(),
                    className,
                    methodName,
                    "Method threw exception",
                    null,
                    durationMs,
                    null,
                    new AppErrorInfo(
                            ex.getClass().getSimpleName(),
                            sanitizer.sanitizeMessage(ex.getMessage())
                    )
            );
            throw ex;
        }
    }
}
