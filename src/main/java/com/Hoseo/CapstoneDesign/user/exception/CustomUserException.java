package com.Hoseo.CapstoneDesign.user.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserException extends GlobalBaseException {
    private UserErrorCode errorCode;
}
