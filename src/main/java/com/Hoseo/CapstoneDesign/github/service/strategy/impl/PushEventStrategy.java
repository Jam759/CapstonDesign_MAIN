package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushEventStrategy implements GithubWebhookStrategy {

    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;
    private final ProjectService projectService;

    @Override
    public boolean supports(String eventType) {
        return "push".equals(eventType);
    }

    @Override
    public void handle(JsonNode payload, String signature256) {
        JsonNode installationIdNode = payload.path("installation").path("id");
        JsonNode repositoryIdNode = payload.path("repository").path("id");
        JsonNode refNode = payload.path("ref");
        JsonNode afterNode = payload.path("after");

        if (installationIdNode.isMissingNode() || installationIdNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        if (repositoryIdNode.isMissingNode() || repositoryIdNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        if (refNode.isMissingNode() || refNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        if (afterNode.isMissingNode() || afterNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }

        long installationId = installationIdNode.asLong();
        long repositoryId = repositoryIdNode.asLong();
        String ref = refNode.asText();
        String commitSha = afterNode.asText();
        GithubAppInstallations installation = gitHubAppInstallationService.getById(installationId);
        InstallationRepository installationRepository =
                installationRepositoryService.getByInstallationAndRepositoryId(installation, repositoryId);
        Projects project
                = projectService.getByInstallationRepository(installationRepository);
//
//        String defaultBranchRef = "refs/heads/" + project.getDefaultBranch();
//        if (!defaultBranchRef.equals(ref)) {
//            return;
//        }
//
//        analysisQueueService.publish(project, installationRepository, commitSha, deliveryId);


    }
}
