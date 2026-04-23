package com.Hoseo.CapstoneDesign.notification.facade;

import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;
import com.Hoseo.CapstoneDesign.notification.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationFacade {
    List<NotificationResponse> getNotification(Long userId, Integer page, Integer size);

    List<NotificationResponse> getUnReadNotification(Long userId);

    void readNotification(Long userId, Long notificationId);

    void readNotifications(Long userId, List<Long> ids);

    void successHandle(NotificationQueueBaseMessage envelope);

    void failedHandle(NotificationQueueBaseMessage envelope);
}
