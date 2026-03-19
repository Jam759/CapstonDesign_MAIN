package com.Hoseo.CapstoneDesign.github.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;

public interface GithubWebhookStrategy {

    boolean supports(String eventType);

    //deliveryId는 멱등성용
    void handle(JsonNode payload, String deliveryId);
}
