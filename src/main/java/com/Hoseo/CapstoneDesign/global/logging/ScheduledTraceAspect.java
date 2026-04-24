package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.global.logging.support.TraceIdContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ScheduledTraceAspect {

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object ensureTraceIdForScheduledExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        try (TraceIdContext ignored = TraceIdContext.open(null)) {
            return joinPoint.proceed();
        }
    }
}
