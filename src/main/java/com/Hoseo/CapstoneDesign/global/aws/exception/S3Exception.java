package com.Hoseo.CapstoneDesign.global.aws.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3Exception extends GlobalBaseException {
    private final S3ErrorCode errorCode;
}
