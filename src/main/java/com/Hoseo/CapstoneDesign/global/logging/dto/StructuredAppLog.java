package com.Hoseo.CapstoneDesign.global.logging.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StructuredAppLog(
        String timestamp,
        String level,
        String service,
        String serverType,
        String category,
        String eventType,
        String traceId,
        String className,
        String method,
        String message,
        Map<String, Object> args,
        Long durationMs,
        Object result,
        AppErrorInfo error
) {
}
