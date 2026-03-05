package com.Hoseo.CapstoneDesign.security.exception;

import lombok.Getter;

@Getter
public class RefreshTokenException extends AuthBaseException {
    private final RefreshTokenErrorCode errorCode;

    public RefreshTokenException(RefreshTokenErrorCode errorCode) {
        super("RefreshTokenException");
        this.errorCode = errorCode;
    }
}
