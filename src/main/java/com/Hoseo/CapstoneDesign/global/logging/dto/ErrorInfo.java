package com.Hoseo.CapstoneDesign.global.logging.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorInfo(
        String message,
        Integer code,
        Integer httpStatus
) {
}