package com.Hoseo.CapstoneDesign.analysis.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectAnalysisReportException extends GlobalBaseException {
    private final ProjectAnalysisReportErrorCode errorCode;
}
