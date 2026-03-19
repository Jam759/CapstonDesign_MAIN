package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InstallationEventStrategy implements GithubWebhookStrategy {

    @Override
    public boolean supports(String eventType) {
        return "installation".equals(eventType);
    }

    @Override
    public void handle(JsonNode payload, String deliveryId) {
        String action = payload.path("action").asText("");
        long installationId = payload.path("installation").path("id").asLong();
        String accountLogin = payload.path("installation").path("account").path("login").asText("");
        String accountType = payload.path("installation").path("account").path("type").asText("");
        String senderLogin = payload.path("sender").path("login").asText("");
        String repositoriesUrl = payload.path("repositories_url").asText("");

        log.info(
                "GitHub installation event received. deliveryId={}, action={}, installationId={}, accountLogin={}, accountType={}, senderLogin={}, repositoriesUrl={}",
                deliveryId,
                action,
                installationId,
                accountLogin,
                accountType,
                senderLogin,
                repositoriesUrl
        );
    }
}
