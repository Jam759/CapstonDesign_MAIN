package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.factory.GitHubEntityFactory;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.GithubAppClientService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallationEventStrategy implements GithubWebhookStrategy {

    private final GithubAppClientService githubAppClientService;
    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;

    @Override
    public boolean supports(String eventType) {
        return "installation".equals(eventType);
    }

    @Override
    public void handle(JsonNode payload, String deliveryId) {
        String action = payload.path("action").asText(null);
        switch (action) {
            case "created" -> createdHandle(payload, deliveryId);
            case "deleted" -> deletedHandle(payload, deliveryId);
            default -> throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
    }

    private void createdHandle(JsonNode payload, String deliveryId) {
        JsonNode installationNode = payload.path("installation");
        long installationId = installationNode.path("id").asLong();
        GithubAppInstallations installation = gitHubAppInstallationService.getByid(installationId);
        JsonNode repositoriesNode = payload.path("repositories");

        List<InstallationRepository> installationRepositories = new ArrayList<>();
        if (!repositoriesNode.isArray()) {
            // repository_selection=all 인 경우 webhook에 전체 목록이 비어있거나 생략된 상황까지 방어
            // 이거는 client 이용해서 끌어오기

            return;
        }else {
            for (JsonNode repoNode : repositoriesNode) {
                InstallationRepository e =
                        GitHubEntityFactory.toInstallationRepository(installation, repoNode);
                installationRepositories.add(e);
            }
        }

        installationRepositoryService.saveAll(installationRepositories);
    }

    private void deletedHandle(JsonNode payload, String deliveryId) {

    }
}
