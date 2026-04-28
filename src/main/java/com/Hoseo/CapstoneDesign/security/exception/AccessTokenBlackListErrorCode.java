package com.Hoseo.CapstoneDesign.security.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AccessTokenBlackListErrorCode implements GlobalErrorCode {

    TOKEN_IS_BLACK_LIST(HttpStatus.UNAUTHORIZED, 1201, "Logged out access token."),
    TOKEN_BLACKLIST_CHECK_FAILED(HttpStatus.UNAUTHORIZED, 1202, "Access token blacklist verification failed.");

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
        return message;
    }
}
