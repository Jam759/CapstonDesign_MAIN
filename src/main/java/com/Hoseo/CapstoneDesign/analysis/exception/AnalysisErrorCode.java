package com.Hoseo.CapstoneDesign.analysis.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnalysisErrorCode implements GlobalErrorCode {

    ANALYSIS_USER_VIEW_NOT_FOUND(HttpStatus.NOT_FOUND, 6104, "사용자 분석 리포트를 찾을 수 없습니다."),
    ANALYSIS_REPORT_STORAGE_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, 6100, "분석 리포트 저장 경로가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}
