package com.Hoseo.CapstoneDesign.auth.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GitHubErrorCode implements GlobalErrorCode {

    GIT_HUB_NOT_FOUND_USER(HttpStatus.NOT_FOUND, 5104, "GitHub에 존재하지 않는 유저입니다."),
    GIT_HUB_NOT_FOUND_INSTALLATION(HttpStatus.NOT_FOUND, 5204 ,"GitHub APP에 존재하지 않는 유저입니다." ),
    GIT_HUB_APP_FORBIDDEN(HttpStatus.FORBIDDEN, 5203 ,"사용자 GitHub에 접근할 수 없습니다. " ),
    GIT_HUB_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5202 , "사용자 저장해 실패하였습니다." ),
    GIT_HUB_APP_EXIST_USER(HttpStatus.BAD_REQUEST,5202 ,"이미 다른 사용자와 연결된 installation 입니다." ),
    GIT_HUB_APP_INVALID(HttpStatus.BAD_REQUEST, 5201 ,"계정 정보가 일치하지 않습니다." );

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
