package com.Hoseo.CapstoneDesign.global.exception;

public abstract class GlobalBaseException extends RuntimeException {
    public abstract GlobalErrorCode getErrorCode();
}
