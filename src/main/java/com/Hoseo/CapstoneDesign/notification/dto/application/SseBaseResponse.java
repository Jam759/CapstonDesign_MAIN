package com.Hoseo.CapstoneDesign.notification.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SseBaseResponse {

    private UUID id;
    private String eventType;
    private LocalDateTime eventAt;
    private Object data;//각 상황에 맞는 DTO넣기
}
