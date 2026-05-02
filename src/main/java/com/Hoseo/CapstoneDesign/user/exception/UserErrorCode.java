package com.Hoseo.CapstoneDesign.user.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum UserErrorCode implements GlobalErrorCode {

    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, 1101, "User not found."),
    USER_PROFILE_UPDATE_REQUEST_INVALID(HttpStatus.BAD_REQUEST, 1102, "Invalid user profile update request.");

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
