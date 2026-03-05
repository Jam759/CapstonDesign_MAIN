package com.Hoseo.CapstoneDesign.auth.exception;

import com.Hoseo.CapstoneDesign.security.exception.AuthBaseException;
import lombok.Getter;

@Getter
public class GitHubException extends AuthBaseException {
    private final GitHubErrorCode errorCode;

    public GitHubException(GitHubErrorCode errorCode) {
        super("GitHubException");
        this.errorCode = errorCode;
    }
}
