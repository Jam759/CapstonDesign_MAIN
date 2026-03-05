package com.Hoseo.CapstoneDesign.security.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum JwtUtilErrorCode implements GlobalErrorCode {

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,6000, "만료된 토큰입니다."),
    TOKEN_BAD_SIGNATURE(HttpStatus.UNAUTHORIZED,6001, "잘못된 토큰입니다."),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED,6001, "잘못된 토큰입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED,6001, "잘못된 토큰입니다."),
    TOKEN_ILLEGAL_ARGUMENT(HttpStatus.UNAUTHORIZED,6004, "잘못된 토큰입니다."),
    TOKEN_OTHER(HttpStatus.INTERNAL_SERVER_ERROR,6005, "서버 오류입니다."),
    TOKEN_IS_NULL(HttpStatus.UNAUTHORIZED, 6006, "토큰이 존재하지 않습니다."),
    TOKEN_VALIDATION_FAIL(HttpStatus.UNAUTHORIZED, 6001, "잘못된 토큰입니다.");

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

