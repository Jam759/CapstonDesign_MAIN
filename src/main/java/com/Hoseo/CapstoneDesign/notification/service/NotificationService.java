package com.Hoseo.CapstoneDesign.notification.service;

import com.Hoseo.CapstoneDesign.notification.dto.application.SseBaseResponse;
import com.Hoseo.CapstoneDesign.notification.entity.SseNotification;
import com.Hoseo.CapstoneDesign.notification.event.SseNotificationDispatchRequestedEvent;
import com.Hoseo.CapstoneDesign.notification.repository.SseNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

//엔티티 관련 서비스 저장,수정 등 처리
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseNotificationRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SseNotification createAndDispatch(SseNotification notification, SseBaseResponse response) {
        SseNotification savedNotification = repository.save(notification);
        applicationEventPublisher.publishEvent(
                new SseNotificationDispatchRequestedEvent(savedNotification.getUser().getUserId(), response)
        );
        return savedNotification;
    }
}
