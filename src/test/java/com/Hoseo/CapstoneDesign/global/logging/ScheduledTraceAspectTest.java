package com.Hoseo.CapstoneDesign.global.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class ScheduledTraceAspectTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void assignsAndClearsTraceIdForScheduledExecution() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new SampleScheduler());
        factory.addAspect(new ScheduledTraceAspect());
        SampleScheduler proxy = factory.getProxy();

        String traceId = proxy.run();

        assertThat(StringUtils.hasText(traceId)).isTrue();
        assertThat(MDC.get(TRACE_ID)).isNull();
    }

    static class SampleScheduler {

        @Scheduled(fixedDelay = 1000)
        String run() {
            return MDC.get(TRACE_ID);
        }
    }
}
