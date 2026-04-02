package com.Hoseo.CapstoneDesign.security.filter;

import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.REQUEST_START_TIME;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;

public class TraceMdcFilter extends OncePerRequestFilter {

    private final LoggingProperties loggingProperties;

    public TraceMdcFilter(LoggingProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return loggingProperties.getExcludePaths()
                .stream()
                .anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());

        String traceId = resolveTraceId(request);
        MDC.put(TRACE_ID, traceId);
        response.setHeader("X-Trace-Id", traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String githubDeliveryId = request.getHeader("X-GitHub-Delivery");
        if (StringUtils.hasText(githubDeliveryId)) {
            return githubDeliveryId;
        }

        String requestTraceId = request.getHeader("X-Trace-Id");
        if (StringUtils.hasText(requestTraceId)) {
            return requestTraceId;
        }

        return UUID.randomUUID().toString();
    }
}
