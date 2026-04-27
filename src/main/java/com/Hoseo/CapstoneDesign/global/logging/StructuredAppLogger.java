package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.common.exception.CommonException;
import com.Hoseo.CapstoneDesign.common.exception.CommonErrorCode;
import com.Hoseo.CapstoneDesign.global.logging.dto.AppErrorInfo;
import com.Hoseo.CapstoneDesign.global.logging.dto.StructuredAppLog;
import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import com.Hoseo.CapstoneDesign.global.logging.support.LogCategory;
import com.Hoseo.CapstoneDesign.global.logging.support.LogSourceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;

@Component
public class StructuredAppLogger {

    private static final Logger log = LoggerFactory.getLogger("STRUCTURED_APP");
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ObjectMapper objectMapper;
    private final LoggingProperties loggingProperties;

    public StructuredAppLogger(ObjectMapper objectMapper, LoggingProperties loggingProperties) {
        this.objectMapper = objectMapper;
        this.loggingProperties = loggingProperties;
    }

    public void trace(
            String eventType,
            String className,
            String methodName,
            String message,
            Map<String, Object> args,
            Long durationMs,
            Object result,
            AppErrorInfo errorInfo
    ) {
        write("TRACE", eventType, className, methodName, message, args, durationMs, result, errorInfo);
    }

    public void debug(
            String eventType,
            String className,
            String methodName,
            String message,
            Map<String, Object> args,
            Long durationMs,
            Object result,
            AppErrorInfo errorInfo
    ) {
        write("DEBUG", eventType, className, methodName, message, args, durationMs, result, errorInfo);
    }

    public void info(
            String eventType,
            String className,
            String methodName,
            String message,
            Map<String, Object> args,
            Long durationMs,
            Object result,
            AppErrorInfo errorInfo
    ) {
        write("INFO", eventType, className, methodName, message, args, durationMs, result, errorInfo);
    }

    public void warn(
            String eventType,
            String className,
            String methodName,
            String message,
            Map<String, Object> args,
            Long durationMs,
            Object result,
            AppErrorInfo errorInfo
    ) {
        write("WARN", eventType, className, methodName, message, args, durationMs, result, errorInfo);
    }

    public void error(
            String eventType,
            String className,
            String methodName,
            String message,
            Map<String, Object> args,
            Long durationMs,
            Object result,
            AppErrorInfo errorInfo
    ) {
        write("ERROR", eventType, className, methodName, message, args, durationMs, result, errorInfo);
    }

    private void write(
            String level,
            String eventType,
            String className,
            String methodName,
            String message,
            Map<String, Object> args,
            Long durationMs,
            Object result,
            AppErrorInfo errorInfo
    ) {
        String source = formatSource(className, methodName);
        StructuredAppLog payload = new StructuredAppLog(
                now(),
                level,
                loggingProperties.getService(),
                loggingProperties.getServerType(),
                LogCategory.APP.name(),
                eventType,
                MDC.get(TRACE_ID),
                className,
                methodName,
                source,
                message,
                args,
                durationMs,
                result,
                errorInfo
        );

        String json = toJson(payload);
        try (LogSourceContext ignored = LogSourceContext.open(source)) {
            switch (level) {
                case "TRACE" -> log.trace(json);
                case "DEBUG" -> log.debug(json);
                case "INFO" -> log.info(json);
                case "WARN" -> log.warn(json);
                case "ERROR" -> log.error(json);
                default -> throw new CommonException(CommonErrorCode.INVALID_INPUT);
            }
        }
    }

    private String now() {
        return OffsetDateTime.now(KST).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{\"message\":\"structured app logging serialization failed\"}";
        }
    }

    private String formatSource(String className, String methodName) {
        return className + "#" + methodName;
    }
}
