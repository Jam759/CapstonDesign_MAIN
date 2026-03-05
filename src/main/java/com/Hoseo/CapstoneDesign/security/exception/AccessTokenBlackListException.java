package com.Hoseo.CapstoneDesign.security.exception;

import lombok.Getter;

@Getter
public class AccessTokenBlackListException extends AuthBaseException {
    private final AccessTokenBlackListErrorCode errorCode;

    public AccessTokenBlackListException(AccessTokenBlackListErrorCode errorCode) {
        super("AccessTokenBlackListException");
        this.errorCode = errorCode;
    }
}
