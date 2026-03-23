package com.Hoseo.CapstoneDesign.project.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectsErrorCode implements GlobalErrorCode {

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, 5004 ,"프로젝트를 찾을 수 없습니다." ),
    PROJECT_FORBIDDEN(HttpStatus.FORBIDDEN, 5005,"프러젝트 접근 권한이 없습니다." ),
    PROJECT_ALREADY_SETTING(HttpStatus.BAD_REQUEST, 5006 , "이미 설정된 프로젝트입니다." );

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}
