package com.Hoseo.CapstoneDesign.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Schema(description = "공통 예외 응답")
public class GlobalExceptionResponse {

    @Schema(description = "애플리케이션 에러 코드", example = "5004")
    private int errorCode;

    @Schema(description = "에러 메시지", example = "프로젝트를 찾을 수 없습니다.")
    private String errorMessage;

    @Schema(description = "HTTP 상태", example = "NOT_FOUND")
    private HttpStatus httpStatus;

    public  GlobalExceptionResponse( GlobalErrorCode e ) {
        this.errorCode = e.getErrorCode();
        this.httpStatus = e.getHttpStatus();
        this.errorMessage = e.getMessage();
    }


}
