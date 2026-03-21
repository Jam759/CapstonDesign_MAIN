package com.Hoseo.CapstoneDesign.notification.service;

import com.Hoseo.CapstoneDesign.notification.dto.application.SseBaseResponse;
import com.Hoseo.CapstoneDesign.notification.repository.SseEmitterRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

//SSE 구독,송신 서비스
@Service
@RequiredArgsConstructor
public class NotificationSseService {

    private final SseEmitterRepository emitterRepository;

    public SseEmitter subscribe(Users user) {
        SseEmitter emitter = new SseEmitter(60_000L);
        Long userId = user.getUserId();
        emitterRepository.save(userId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(userId));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitterRepository.delete(userId);
        });
        emitter.onError((e) -> emitterRepository.delete(userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .id(UUID.randomUUID().toString())
                    .data("SSE connected"));
        } catch (IOException e) {
            emitterRepository.delete(userId);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendNotification(Long userId, SseBaseResponse<?> response) {
        SseEmitter emitter = emitterRepository.get(userId);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .name(response.getEventType())
                    .id(response.getId().toString())
                    .data(response));
        } catch (IOException e) {
            emitterRepository.delete(userId);
            emitter.completeWithError(e);
        }
    }
}
