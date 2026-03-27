package com.Hoseo.CapstoneDesign.notification.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailMessage {
    private String errorCode;
    private String errorMessage;
    private Integer HTTPStatus;
    private Boolean retryable;
}
