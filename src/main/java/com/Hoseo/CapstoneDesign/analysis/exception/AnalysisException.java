package com.Hoseo.CapstoneDesign.analysis.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalysisException extends GlobalBaseException {
    private final AnalysisErrorCode errorCode;
}
