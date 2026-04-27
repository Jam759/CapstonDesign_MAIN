package com.Hoseo.CapstoneDesign.project.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectsErrorCode implements GlobalErrorCode {

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "Project not found."),
    PROJECT_FORBIDDEN(HttpStatus.FORBIDDEN, 4002, "Project access is forbidden."),
    PROJECT_ALREADY_SETTING(HttpStatus.CONFLICT, 4003, "Project GitHub setting already exists."),
    PROJECT_INVITE_NOT_FOUND(HttpStatus.NOT_FOUND, 4004, "Project invite not found."),
    PROJECT_INVITE_DUPLICATED(HttpStatus.CONFLICT, 4005, "Project invite already exists."),
    PROJECT_INVITE_INVALID_STATUS(HttpStatus.BAD_REQUEST, 4006, "Project invite status is invalid."),
    PROJECT_INVITE_INVALID_REQUEST(HttpStatus.BAD_REQUEST, 4007, "Project invite request is invalid.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}
