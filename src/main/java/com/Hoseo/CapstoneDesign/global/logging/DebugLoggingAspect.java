package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
@ConditionalOnProperty(prefix = "app.logging.debug-aspect", name = "enabled", havingValue = "true")
public class DebugLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger("[DEBUG_ASPECT]");
    private static final Set<String> OBJECT_METHOD_NAMES = Set.of("toString", "hashCode", "equals");

    private final LoggingProperties loggingProperties;
    private final DebugLogSanitizer sanitizer;

    public DebugLoggingAspect(LoggingProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
        this.sanitizer = new DebugLogSanitizer(loggingProperties.getDebugAspect());
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

        log.info("[ENTER] {}.{} args={}", className, methodName, args);

        long startNanos = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;

            if (loggingProperties.getDebugAspect().isLogReturnValue()) {
                log.info(
                        "[EXIT] {}.{} durationMs={} result={}",
                        className,
                        methodName,
                        durationMs,
                        sanitizer.sanitizeReturnValue(result)
                );
            } else {
                log.info("[EXIT] {}.{} durationMs={}", className, methodName, durationMs);
            }

            return result;
        } catch (Throwable ex) {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
            log.info(
                    "[THROW] {}.{} durationMs={} exception={} message={}",
                    className,
                    methodName,
                    durationMs,
                    ex.getClass().getSimpleName(),
                    sanitizer.sanitizeMessage(ex.getMessage())
            );
            throw ex;
        }
    }
}
