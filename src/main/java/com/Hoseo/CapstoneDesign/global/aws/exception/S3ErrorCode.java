package com.Hoseo.CapstoneDesign.global.aws.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements GlobalErrorCode {

    S3_OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, 40401, "S3 객체를 찾을 수 없습니다."),
    S3_ACCESS_DENIED(HttpStatus.FORBIDDEN, 40301, "S3 접근 권한이 없습니다."),
    S3_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "S3 I/O 처리 중 오류가 발생했습니다."),
    S3_JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50002, "S3 JSON 파싱에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

}
