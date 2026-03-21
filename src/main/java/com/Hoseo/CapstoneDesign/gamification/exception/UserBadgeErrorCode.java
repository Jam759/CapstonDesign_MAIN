package com.Hoseo.CapstoneDesign.gamification.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum UserBadgeErrorCode implements GlobalErrorCode {

    USER_BADGE_SAVE_ERROR(HttpStatus.BAD_REQUEST, 2002, "유저 뱃지 저장에 실패하였습니다.");
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
