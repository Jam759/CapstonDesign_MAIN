package com.Hoseo.CapstoneDesign.github.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InstallationRepositoryErrorCode implements GlobalErrorCode {

    INSTALLATION_REPOSITORY_NOT_FOUND(HttpStatus.NOT_FOUND, 6004, "깃허브 리포지토리를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}

