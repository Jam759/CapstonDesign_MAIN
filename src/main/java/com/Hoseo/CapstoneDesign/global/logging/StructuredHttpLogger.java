package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import com.Hoseo.CapstoneDesign.global.logging.dto.ErrorInfo;
import com.Hoseo.CapstoneDesign.global.logging.dto.HttpInfo;
import com.Hoseo.CapstoneDesign.global.logging.dto.StructuredHttpLog;
import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.HANDLER_CLASS_NAME;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.HANDLER_METHOD_NAME;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.REQUEST_START_TIME;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;

@Component
public class StructuredHttpLogger {

    private static final Logger log = LoggerFactory.getLogger("STRUCTURED_HTTP");
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ObjectMapper objectMapper;
    private final LoggingProperties loggingProperties;

    public StructuredHttpLogger(ObjectMapper objectMapper, LoggingProperties loggingProperties) {
        this.objectMapper = objectMapper;
        this.loggingProperties = loggingProperties;
    }

    public void info(
            String category,
            String eventType,
            String className,
            String methodName,
            String message,
            Map<String, Object> args,
            HttpServletRequest request,
            Integer status,
            Long durationMs
    ) {
        StructuredHttpLog payload = new StructuredHttpLog(
                now(),
                "INFO",
                loggingProperties.getService(),
                loggingProperties.getServerType(),
                category,
                eventType,
                MDC.get(TRACE_ID),
                className,
                methodName,
                message,
                args,
                new HttpInfo(
                        request.getMethod(),
                        request.getRequestURI(),
                        status
                ),
                durationMs,
                null
        );

        log.info(toJson(payload));
    }

    public void error(
            String category,
            String eventType,
            String className,
            String methodName,
            String message,
            Map<String, Object> args,
            HttpServletRequest request,
            Integer status,
            Long durationMs,
            ErrorInfo errorInfo
    ) {
        StructuredHttpLog payload = new StructuredHttpLog(
                now(),
                "ERROR",
                loggingProperties.getService(),
                loggingProperties.getServerType(),
                category,
                eventType,
                MDC.get(TRACE_ID),
                className,
                methodName,
                message,
                args,
                new HttpInfo(
                        request.getMethod(),
                        request.getRequestURI(),
                        status
                ),
                durationMs,
                errorInfo
        );

        log.error(toJson(payload));
    }

    public long resolveDurationMs(HttpServletRequest request) {
        Object start = request.getAttribute(REQUEST_START_TIME);
        if (start instanceof Long startTime) {
            return System.currentTimeMillis() - startTime;
        }
        return 0L;
    }

    public ErrorInfo toErrorInfo(GlobalErrorCode errorCode) {
        return new ErrorInfo(
                errorCode.getMessage(),
                errorCode.getErrorCode(),
                errorCode.getHttpStatus().value()
        );
    }

    public String resolveHandlerClassName(HttpServletRequest request, String fallback) {
        Object value = request.getAttribute(HANDLER_CLASS_NAME);
        return value != null ? value.toString() : fallback;
    }

    public String resolveHandlerMethodName(HttpServletRequest request, String fallback) {
        Object value = request.getAttribute(HANDLER_METHOD_NAME);
        return value != null ? value.toString() : fallback;
    }

    private String now() {
        return OffsetDateTime.now(KST).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{\"message\":\"structured logging serialization failed\"}";
        }
    }
}
