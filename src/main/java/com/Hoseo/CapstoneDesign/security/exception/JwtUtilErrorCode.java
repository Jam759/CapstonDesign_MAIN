package com.Hoseo.CapstoneDesign.security.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum JwtUtilErrorCode implements GlobalErrorCode {

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 1401, "만료된 토큰입니다."),
    TOKEN_BAD_SIGNATURE(HttpStatus.UNAUTHORIZED, 1402, "잘못된 토큰입니다."),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, 1403, "잘못된 토큰입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, 1404, "잘못된 토큰입니다."),
    TOKEN_ILLEGAL_ARGUMENT(HttpStatus.UNAUTHORIZED, 1405, "잘못된 토큰입니다."),
    TOKEN_OTHER(HttpStatus.UNAUTHORIZED, 1406, "잘못된 토큰입니다."),
    TOKEN_IS_NULL(HttpStatus.UNAUTHORIZED, 1407, "토큰이 존재하지 않습니다."),
    TOKEN_VALIDATION_FAIL(HttpStatus.UNAUTHORIZED, 1408, "잘못된 토큰입니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

