package com.Hoseo.CapstoneDesign.notification.service;

import com.Hoseo.CapstoneDesign.notification.repository.SseNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//엔티티 관련 서비스 저장,수정 등 처리
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseNotificationRepository repository;

}
