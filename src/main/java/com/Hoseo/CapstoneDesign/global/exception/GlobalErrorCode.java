package com.Hoseo.CapstoneDesign.global.exception;

import org.springframework.http.HttpStatus;

public interface GlobalErrorCode {
    HttpStatus getHttpStatus(); // 상태값
    int getErrorCode(); // 에러코드
    String getMessage(); // 메시지
}
