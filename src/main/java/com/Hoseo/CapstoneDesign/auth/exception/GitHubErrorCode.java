package com.Hoseo.CapstoneDesign.auth.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GitHubErrorCode implements GlobalErrorCode {

    GIT_HUB_NOT_FOUND_USER(HttpStatus.NOT_FOUND, 5104, "GitHub에 존재하지 않는 유저입니다.");

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
