package com.Hoseo.CapstoneDesign.github.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import com.Hoseo.CapstoneDesign.security.exception.AuthBaseException;
import lombok.Getter;

@Getter
public class GitHubException extends GlobalBaseException {
    private final GitHubErrorCode errorCode;

    public GitHubException(GitHubErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
