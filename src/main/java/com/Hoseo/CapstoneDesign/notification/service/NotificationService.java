package com.Hoseo.CapstoneDesign.notification.service;

import com.Hoseo.CapstoneDesign.notification.dto.application.SseBaseResponse;
import com.Hoseo.CapstoneDesign.notification.entity.SseNotification;
import com.Hoseo.CapstoneDesign.notification.event.SseNotificationDispatchRequestedEvent;
import com.Hoseo.CapstoneDesign.notification.exception.NotificationErrorCode;
import com.Hoseo.CapstoneDesign.notification.exception.NotificationException;
import com.Hoseo.CapstoneDesign.notification.repository.SseNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<SseNotification> getNotifications(Long userId, Integer page, Integer size) {
        // API pages are 1-based, while PageRequest is 0-based.
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : size;

        return repository.findByUserUserId(
                        userId,
                        PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"))
                )
                .getContent();
    }

    public List<SseNotification> getUnreadNotifications(Long userId) {
        return repository.findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public void readNotification(Long userId, Long notificationId) {
        SseNotification notification = repository
                .findBySseNotificationIdAndUserUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
    }

    public void readNotifications(Long userId, List<Long> ids) {
        repository.findBySseNotificationIdInAndUserUserId(ids, userId)
                .forEach(SseNotification::markAsRead);
    }
}
