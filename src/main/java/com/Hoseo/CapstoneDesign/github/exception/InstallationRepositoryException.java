package com.Hoseo.CapstoneDesign.github.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InstallationRepositoryException extends GlobalBaseException {
    private final InstallationRepositoryErrorCode errorCode;
}
