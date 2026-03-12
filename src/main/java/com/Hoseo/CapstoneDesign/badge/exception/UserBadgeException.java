package com.Hoseo.CapstoneDesign.badge.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBadgeException extends GlobalBaseException {
    private final UserBadgeErrorCode errorCode;

}
