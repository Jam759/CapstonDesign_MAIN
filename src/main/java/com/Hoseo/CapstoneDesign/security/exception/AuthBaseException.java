package com.Hoseo.CapstoneDesign.security.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import org.springframework.security.core.AuthenticationException;

public abstract class AuthBaseException extends AuthenticationException {

    public AuthBaseException(String msg) {
        super(msg);
    }
    public abstract GlobalErrorCode getErrorCode();
}