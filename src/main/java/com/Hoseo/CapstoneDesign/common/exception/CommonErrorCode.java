package com.Hoseo.CapstoneDesign.common.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements GlobalErrorCode {

    COMMON_CODE_INVALID(HttpStatus.BAD_REQUEST, 2001, "유효하지 않은 공통 코드입니다."),
    INVALID_INPUT(HttpStatus.INTERNAL_SERVER_ERROR, 2002, "잘못된 입력값입니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}
