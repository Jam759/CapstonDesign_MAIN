package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisDtoFactory;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisJobEntityFactory;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.github.dto.application.PullRequestWebhookContext;
import com.Hoseo.CapstoneDesign.github.dto.query.GitHubWebhookValidationQueryResult;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.GitHubQueryService;
import com.Hoseo.CapstoneDesign.github.service.GithubAppClientService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.Hoseo.CapstoneDesign.github.util.GitHubWebhookPayloadUtil;
import com.Hoseo.CapstoneDesign.global.aws.properties.SqsProperties;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsBaseMessage;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsMessageSender;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PullRequestStrategy implements GithubWebhookStrategy {

    private final GitHubQueryService gitHubQueryService;
    private final GithubAppClientService githubAppClientService;
    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;
    private final SqsMessageSender sqsMessageSender;
    private final SqsProperties sqsProperties;
    private final AnalysisJobService analysisJobService;

    @Override
    public boolean supports(String eventType) {
        return "pull_request".equals(eventType);
    }

    @Override
    public void handle(JsonNode payload, String deliveryId) {
        PullRequestWebhookContext context = parse(payload, deliveryId);
        GitHubWebhookValidationQueryResult validateResult =
                gitHubQueryService.validateWebhook(
                        context.installationId(),
                        context.repositoryId(),
                        context.senderId()
                );
        validate(validateResult);

        switch (context.action()) {
            case "closed" -> handleClosed(context, validateResult);
            case "opened" -> handleOpened(context, validateResult);
            default -> throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
    }

    private void handleOpened(
            PullRequestWebhookContext context,
            GitHubWebhookValidationQueryResult validateResult
    ) {
        // TODO:
        // 현행 처리 로직 미구현
    }

    private void handleClosed(
            PullRequestWebhookContext context,
            GitHubWebhookValidationQueryResult validateResult
    ) {
        if (!context.merged()) {
            return;
        }

        String beforeCommit = context.baseSha();
        String afterCommit = context.mergeCommitSha();
        String branchName = context.baseRef();

        if (!validateResult.trackedBranch().equals(branchName)) {
            return;
        }

        AnalysisJob job = AnalysisJobEntityFactory.toAnalysisJob(
                gitHubAppInstallationService.getReferenceById(validateResult.matchedInstallationId()),
                installationRepositoryService.getReferenceById(validateResult.installationRepositoryId()),
                afterCommit,
                beforeCommit,
                branchName,
                context.deliveryId()
        );

        AnalysisJob savedJob;
        try {
            savedJob = analysisJobService.create(job);
        } catch (DuplicateKeyException e) {
            return;
        }

        SqsBaseMessage message =
                AnalysisDtoFactory.toSqsAnalysisQueueMessage(savedJob, validateResult);
        sqsMessageSender.send(sqsProperties.analysisQueue(), message);
        savedJob.updateJobStatus(AnalysisJobStatus.ANALYSIS_JOB_QUEUED);
        analysisJobService.create(savedJob);
    }

    private PullRequestWebhookContext parse(JsonNode payload, String deliveryId) {
        return new PullRequestWebhookContext(
                GitHubWebhookPayloadUtil.requireText(payload, "action"),
                GitHubWebhookPayloadUtil.optionalLong(payload, "installation", "id"),
                GitHubWebhookPayloadUtil.requireLong(payload, "repository", "id"),
                GitHubWebhookPayloadUtil.requireText(payload, "repository", "full_name"),
                GitHubWebhookPayloadUtil.optionalLong(payload, "sender", "id"),
                GitHubWebhookPayloadUtil.requireLong(payload, "pull_request", "number"),
                GitHubWebhookPayloadUtil.requireText(payload, "pull_request", "base", "ref"),
                GitHubWebhookPayloadUtil.requireText(payload, "pull_request", "base", "sha"),
                GitHubWebhookPayloadUtil.requireText(payload, "pull_request", "head", "ref"),
                GitHubWebhookPayloadUtil.requireText(payload, "pull_request", "head", "sha"),
                payload.path("pull_request").path("merged").asBoolean(false),
                GitHubWebhookPayloadUtil.optionalText(payload, "pull_request", "merge_commit_sha"),
                deliveryId
        );
    }

    private void validate(GitHubWebhookValidationQueryResult validateResult) {
        if (validateResult == null) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }

        if (!githubAppClientService.isRepositoryCollaborator(
                validateResult.matchedInstallationId(),
                validateResult.repositoryFullName(),
                validateResult.installationAccountLogin()
        )) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }
    }
}
