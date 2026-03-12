package com.Hoseo.CapstoneDesign.notification.repository;

import com.Hoseo.CapstoneDesign.notification.entity.SseNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SseNotificationRepository extends JpaRepository<SseNotification, Long> {
}
