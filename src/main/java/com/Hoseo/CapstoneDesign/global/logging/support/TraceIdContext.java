package com.Hoseo.CapstoneDesign.global.logging.support;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;

public final class TraceIdContext implements AutoCloseable {

    private final String previousTraceId;
    private final String currentTraceId;
    private final boolean changed;

    private TraceIdContext(String previousTraceId, String currentTraceId, boolean changed) {
        this.previousTraceId = previousTraceId;
        this.currentTraceId = currentTraceId;
        this.changed = changed;
    }

    public static TraceIdContext open(String requestedTraceId) {
        String previousTraceId = MDC.get(TRACE_ID);
        String currentTraceId = requestedTraceId;

        if (!StringUtils.hasText(currentTraceId)) {
            currentTraceId = previousTraceId;
        }
        if (!StringUtils.hasText(currentTraceId)) {
            currentTraceId = UUID.randomUUID().toString();
        }

        boolean changed = !Objects.equals(previousTraceId, currentTraceId);
        if (changed) {
            MDC.put(TRACE_ID, currentTraceId);
        }

        return new TraceIdContext(previousTraceId, currentTraceId, changed);
    }

    public String traceId() {
        return currentTraceId;
    }

    @Override
    public void close() {
        if (!changed) {
            return;
        }

        if (StringUtils.hasText(previousTraceId)) {
            MDC.put(TRACE_ID, previousTraceId);
            return;
        }

        MDC.remove(TRACE_ID);
    }
}
