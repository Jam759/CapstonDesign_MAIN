package com.Hoseo.CapstoneDesign.global.logging.support;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.LOG_SOURCE;

public final class LogSourceContext implements AutoCloseable {

    private final String previousSource;
    private final boolean changed;

    private LogSourceContext(String previousSource, boolean changed) {
        this.previousSource = previousSource;
        this.changed = changed;
    }

    public static LogSourceContext open(String source) {
        String previousSource = MDC.get(LOG_SOURCE);
        boolean changed = !source.equals(previousSource);

        if (changed) {
            MDC.put(LOG_SOURCE, source);
        }

        return new LogSourceContext(previousSource, changed);
    }

    @Override
    public void close() {
        if (!changed) {
            return;
        }

        if (StringUtils.hasText(previousSource)) {
            MDC.put(LOG_SOURCE, previousSource);
            return;
        }

        MDC.remove(LOG_SOURCE);
    }
}
