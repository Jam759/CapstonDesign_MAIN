package com.Hoseo.CapstoneDesign.analysis.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnalysisJobErrorCode implements GlobalErrorCode {

    ANALYSIS_JOB_NOT_FOUND(HttpStatus.NOT_FOUND, 3004, "존재하지 않는 JOB");


    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}
