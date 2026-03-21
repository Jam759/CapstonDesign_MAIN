package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.factory.GitHubEntityFactory;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallationRepositoriesStrategy implements GithubWebhookStrategy {

    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;

    @Override
    public boolean supports(String eventType) {
        return "installation_repositories".equals(eventType);
    }

    @Override
    public void handle(JsonNode payload, String deliveryId) {
        String action = payload.path("action").asText(null);
        switch (action) {
            case "added" -> added(payload);
            case "removed" -> removed(payload);
            default -> throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
    }

    private void added(JsonNode payload) {
        JsonNode installationNode = payload.path("installation");
        long installationId = installationNode.path("id").asLong();
        GithubAppInstallations installations = gitHubAppInstallationService.getById(installationId);
        JsonNode repositoriesAdded = payload.path("repositories_added");

        if (!repositoriesAdded.isArray()) {
            return;
        }

        List<InstallationRepository> installationRepositories = new ArrayList<>();
        for (JsonNode repoNode : repositoriesAdded) {
            InstallationRepository installationRepository =
                    GitHubEntityFactory.toInstallationRepository(installations, repoNode);
            installationRepositories.add(installationRepository);
        }

        if (installationRepositories.isEmpty()) {
            return;
        }
        installationRepositoryService.bulkInsert(installationRepositories);
    }

    private void removed(JsonNode payload) {
        JsonNode installationNode = payload.path("installation");
        JsonNode installationIdNode = installationNode.path("id");

        if (installationIdNode.isMissingNode() || installationIdNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }

        long installationId = installationIdNode.asLong();
        GithubAppInstallations installations = gitHubAppInstallationService.getById(installationId);

        JsonNode repositoriesRemoved = payload.path("repositories_removed");
        if (!repositoriesRemoved.isArray()) {
            return;
        }

        List<Long> repositoryIds = new ArrayList<>();

        for (JsonNode repoNode : repositoriesRemoved) {
            JsonNode repoIdNode = repoNode.path("id");
            if (repoIdNode.isMissingNode() || repoIdNode.isNull()) {
                continue;
            }
            repositoryIds.add(repoIdNode.asLong());
        }

        if (repositoryIds.isEmpty()) {
            return;
        }

        installationRepositoryService.deleteAllByInstallationAndRepositoryIds(installations, repositoryIds);
    }
}
