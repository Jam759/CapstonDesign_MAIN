package com.Hoseo.CapstoneDesign.friend.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum FriendErrorCode implements GlobalErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 7001, "존재하지 않는 사용자입니다."),
    FRIENDSHIP_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 7002, "이미 친구이거나 요청이 존재합니다."),
    FRIENDSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, 7003, "친구 요청을 찾을 수 없습니다.");

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
