package com.Hoseo.CapstoneDesign.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalExceptionResponse {

    private int errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;

    public  GlobalExceptionResponse( GlobalErrorCode e ) {
        this.errorCode = e.getErrorCode();
        this.httpStatus = e.getHttpStatus();
        this.errorMessage = e.getMessage();
    }

}
