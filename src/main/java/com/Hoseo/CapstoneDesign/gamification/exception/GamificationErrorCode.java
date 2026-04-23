package com.Hoseo.CapstoneDesign.gamification.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GamificationErrorCode implements GlobalErrorCode {

    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, 6001, "존재하지 않는 퀘스트입니다."),
    USER_META_NOT_FOUND(HttpStatus.NOT_FOUND, 6002, "사용자 경험치 정보가 존재하지 않습니다.");

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
