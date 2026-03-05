package com.Hoseo.CapstoneDesign.security.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum RefreshTokenErrorCode implements GlobalErrorCode {

    REFRESH_TOKEN_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 4001, "로그인 정보 저장 실패"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, 4004, "로그인 정보 검색 실패"),
    REFRESH_TOKEN_USER_MISMATCH(HttpStatus.UNAUTHORIZED, 4401, "로그인 정보 사용자 불일치"),
    REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, 4402, "폐기된 로그인 정보"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 4403, "만료된 로그인 정보"),
    REFRESH_TOKEN_REUSE_DETECTED(HttpStatus.UNAUTHORIZED, 4405, "로그인 정보 재사용 감지");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
