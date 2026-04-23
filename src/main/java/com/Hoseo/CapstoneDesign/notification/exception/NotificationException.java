package com.Hoseo.CapstoneDesign.notification.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationException extends GlobalBaseException {
    private final NotificationErrorCode errorCode;
}
