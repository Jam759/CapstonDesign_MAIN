package com.Hoseo.CapstoneDesign.notification.repository;

import com.Hoseo.CapstoneDesign.notification.entity.SseNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SseNotificationRepository extends JpaRepository<SseNotification, Long> {

    Page<SseNotification> findByUserUserId(Long userId, Pageable pageable);

    List<SseNotification> findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    Optional<SseNotification> findBySseNotificationIdAndUserUserId(Long sseNotificationId, Long userId);

    List<SseNotification> findBySseNotificationIdInAndUserUserId(List<Long> ids, Long userId);
}
