package com.Hoseo.CapstoneDesign.common.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonException extends GlobalBaseException {
    private final CommonErrorCode errorCode;
}
