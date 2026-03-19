package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class PushEventStrategy implements GithubWebhookStrategy {

    @Override
    public boolean supports(String eventType) {
        return "push".equals(eventType);
    }

    @Override
    public void handle(JsonNode payload, String signature256) {
        String repository = payload.path("repository").path("full_name").asText();
        String pusher = payload.path("pusher").path("name").asText();
        String ref = payload.path("ref").asText();

        // 예:
        // 1. DB 저장
        // 2. 분석 큐(SQS) 적재
        // 3. 브랜치 정보 파싱
    }
}
