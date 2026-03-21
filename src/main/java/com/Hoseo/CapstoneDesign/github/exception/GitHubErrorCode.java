package com.Hoseo.CapstoneDesign.github.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GitHubErrorCode implements GlobalErrorCode {

    GIT_HUB_NOT_FOUND_USER(HttpStatus.NOT_FOUND, 5104, "GitHub에 존재하지 않는 유저입니다."),
    GIT_HUB_NOT_FOUND_INSTALLATION(HttpStatus.NOT_FOUND, 5204 ,"GitHub App에 존재하지 않는 사용자입니다."),
    GIT_HUB_APP_FORBIDDEN(HttpStatus.FORBIDDEN, 5203 ,"사용자의 GitHub 정보에 접근할 수 없습니다."),
    GIT_HUB_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5202 , "사용자 정보 저장에 실패하였습니다."),
    GIT_HUB_APP_EXIST_USER(HttpStatus.BAD_REQUEST, 5205 ,"이미 다른 사용자와 연결된 installation입니다."),
    GIT_HUB_APP_INVALID(HttpStatus.BAD_REQUEST, 5201 ,"계정 정보가 일치하지 않습니다."),
    GIT_HUB_STATE_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5200 ,"가입 정보 생성에 실패하였습니다."),

    GIT_HUB_PRIVATE_KEY_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, 5206, "GitHub App private key 값이 비어 있습니다."),
    GIT_HUB_PRIVATE_KEY_INVALID_FORMAT(HttpStatus.INTERNAL_SERVER_ERROR, 5207, "GitHub App private key 형식이 올바르지 않습니다."),
    GIT_HUB_PRIVATE_KEY_LOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5208, "GitHub App private key 로딩에 실패하였습니다."),
    GIT_HUB_APP_JWT_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5209, "GitHub App JWT 생성에 실패하였습니다."),

    GIT_HUB_WEBHOOK_SIGNATURE_MISSING(HttpStatus.BAD_REQUEST, 5210, "GitHub 웹훅 요청에 서명 정보가 없습니다."),
    GIT_HUB_WEBHOOK_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, 5211, "GitHub 웹훅 서명이 유효하지 않습니다."),
    GIT_HUB_WEBHOOK_SIGNATURE_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5212, "GitHub 웹훅 서명 검증 처리 중 오류가 발생하였습니다."), 
    GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR(HttpStatus.BAD_REQUEST, 5212 ,"지원하지 않는 서비스" );

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