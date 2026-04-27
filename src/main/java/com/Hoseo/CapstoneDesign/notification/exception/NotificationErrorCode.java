package com.Hoseo.CapstoneDesign.notification.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements GlobalErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 8001, "Notification not found."),
    NOTIFICATION_EVENT_TYPE_NULL(HttpStatus.INTERNAL_SERVER_ERROR, 8002, "알림 이벤트 타입이 누락되었습니다."),
    NOTIFICATION_USER_RESOLVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 8003, "알림 대상 사용자를 확인할 수 없습니다."),
    NOTIFICATION_SERIALIZE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 8004, "SSE 페이로드 직렬화에 실패하였습니다."),
    NOTIFICATION_SQS_PROCESS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 8005, "SQS 메시지 처리 중 오류가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}
