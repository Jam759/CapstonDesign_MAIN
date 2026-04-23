package com.Hoseo.CapstoneDesign.notification.dto.application;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("HTTPStatus")
    @JsonAlias("httpStatus")
    private Integer HTTPStatus;
    private Boolean retryable;
}
