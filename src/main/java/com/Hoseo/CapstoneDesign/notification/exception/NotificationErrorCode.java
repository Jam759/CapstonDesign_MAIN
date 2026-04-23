package com.Hoseo.CapstoneDesign.notification.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements GlobalErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 7001, "Notification not found.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}
