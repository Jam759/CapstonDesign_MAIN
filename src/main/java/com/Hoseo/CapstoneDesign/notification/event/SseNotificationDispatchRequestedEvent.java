package com.Hoseo.CapstoneDesign.notification.event;

import com.Hoseo.CapstoneDesign.notification.dto.application.SseBaseResponse;

public record SseNotificationDispatchRequestedEvent(
        Long userId,
        SseBaseResponse response
) {
}
