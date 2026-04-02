package com.Hoseo.CapstoneDesign.global.logging.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StructuredHttpLog(
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
        HttpInfo http,
        Long durationMs,
        ErrorInfo error
) {
}
