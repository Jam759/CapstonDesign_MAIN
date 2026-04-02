package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.github.dto.application.GithubRepositorySummary;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.factory.GitHubEntityFactory;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.GithubAppClientService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.Hoseo.CapstoneDesign.github.util.GitHubWebhookPayloadUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
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
    @Transactional(readOnly = false)
    public void handle(JsonNode payload, String deliveryId) {
        String action = payload.path("action").asText(null);
        switch (action) {
            case "created" -> createdHandle(payload);
            case "deleted" -> deletedHandle(payload);
            default -> throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
    }

    private void createdHandle(JsonNode payload) {
        long installationId = GitHubWebhookPayloadUtil.requireLong(payload, "installation", "id");
        long accountId = GitHubWebhookPayloadUtil.requireLong(payload, "installation", "account", "id");
        String accountLogin = GitHubWebhookPayloadUtil.requireText(payload, "installation", "account", "login");

        JsonNode repositoriesNode = payload.path("repositories");
        List<InstallationRepository> installationRepositories = new ArrayList<>();

        if (!repositoriesNode.isArray()) {
            String installationToken =
                    githubAppClientService.createInstallationAccessToken(installationId);
            List<GithubRepositorySummary> repos =
                    githubAppClientService.getAllAccessibleRepositories(installationToken);
            installationRepositories = GitHubEntityFactory.toInstallationRepositories(repos);
        } else {
            for (JsonNode repoNode : repositoriesNode) {
                InstallationRepository installationRepository =
                        GitHubEntityFactory.toInstallationRepository(repoNode);
                installationRepositories.add(installationRepository);
            }
        }

        if (installationRepositories.isEmpty()) {
            return;
        }

        GithubAppInstallations installation =
                gitHubAppInstallationService.createOrRefresh(installationId, accountId, accountLogin);
        installationRepositories.forEach(repository -> repository.markGithubAppInstallation(installation));
        installationRepositoryService.bulkInsert(installationRepositories);
    }

    private void deletedHandle(JsonNode payload) {
        long installationId = GitHubWebhookPayloadUtil.requireLong(payload, "installation", "id");

        GithubAppInstallations installation = gitHubAppInstallationService.getById(installationId);
        installationRepositoryService.deleteAllByInstallation(installation);
        gitHubAppInstallationService.delete(installation);
        gitHubAppInstallationService.deleteUserGitHubInstallationByGithubAppInstallation(installation);
    }
}
