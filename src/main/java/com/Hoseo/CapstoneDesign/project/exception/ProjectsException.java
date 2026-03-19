package com.Hoseo.CapstoneDesign.project.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectsException extends GlobalBaseException {
    private final ProjectsErrorCode errorCode;
}
