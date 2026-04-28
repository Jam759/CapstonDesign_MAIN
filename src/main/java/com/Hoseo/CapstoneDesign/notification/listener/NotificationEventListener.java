package com.Hoseo.CapstoneDesign.notification.listener;

import com.Hoseo.CapstoneDesign.notification.event.SseNotificationDispatchRequestedEvent;
import com.Hoseo.CapstoneDesign.notification.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationSseService notificationSseService;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatchSseNotification(SseNotificationDispatchRequestedEvent event) {
        notificationSseService.sendNotification(event.userId(), event.response());
    }
}
