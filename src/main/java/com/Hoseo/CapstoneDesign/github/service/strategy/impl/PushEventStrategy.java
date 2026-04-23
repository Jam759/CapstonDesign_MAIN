package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisJobEntityFactory;
import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisEventType;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.github.dto.query.GitHubWebhookValidationQueryResult;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.GitHubQueryService;
import com.Hoseo.CapstoneDesign.github.service.GithubAppClientService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.Hoseo.CapstoneDesign.github.util.GitHubWebhookPayloadUtil;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushEventStrategy implements GithubWebhookStrategy {

    private final GitHubQueryService gitHubQueryService;
    private final GithubAppClientService githubAppClientService;
    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;
    private final AnalysisJobService analysisJobService;
    private final ProjectService projectService;
    private final UserService userService;

    @Override
    public boolean supports(String eventType) {
        return "push".equals(eventType);
    }

    @Override
    public void handle(JsonNode payload, String deliveryId) {
        if (analysisJobService.existsByDeliveryId(deliveryId)) {
            return;
        }

        long installationId = GitHubWebhookPayloadUtil.requireLong(payload, "installation", "id");
        long repositoryId = GitHubWebhookPayloadUtil.requireLong(payload, "repository", "id");
        GitHubWebhookPayloadUtil.requireText(payload, "repository", "full_name");
        String branchName = GitHubWebhookPayloadUtil.extractBranchName(
                GitHubWebhookPayloadUtil.requireText(payload, "ref")
        );
        String before = GitHubWebhookPayloadUtil.requireText(payload, "before");
        String after = GitHubWebhookPayloadUtil.requireText(payload, "after");
        Long senderId = GitHubWebhookPayloadUtil.optionalLong(payload, "sender", "id");

        GitHubWebhookValidationQueryResult validateResult =
                gitHubQueryService.validateWebhook(installationId, repositoryId, senderId);

        if (validateResult == null) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }

        String trackedBranch = validateResult.trackedBranch();
        if (!trackedBranch.equals(branchName)) {
            return;
        }

        if (!githubAppClientService.isRepositoryCollaborator(
                validateResult.matchedInstallationId(),
                validateResult.repositoryFullName(),
                validateResult.installationAccountLogin()
        )) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }

        AnalysisJob job = AnalysisJobEntityFactory.toAnalysisJob(
                projectService.getReferenceById(validateResult.projectId()),
                userService.getReferenceById(validateResult.userId()),
                gitHubAppInstallationService.getReferenceById(validateResult.matchedInstallationId()),
                installationRepositoryService.getReferenceById(validateResult.installationRepositoryId()),
                after,
                before,
                branchName,
                deliveryId,
                AnalysisEventType.NORMAL_ANALYSIS_REQUEST,
                Boolean.TRUE.equals(validateResult.isPrivate())
        );

        try {
            analysisJobService.createPendingJob(job);
        } catch (DuplicateKeyException e) {
            return;
        }
    }
}
