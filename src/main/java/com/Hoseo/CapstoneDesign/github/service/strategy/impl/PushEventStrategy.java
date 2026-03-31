package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisDtoFactory;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisJobEntityFactory;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.github.dto.query.GitHubWebhookValidationQueryResult;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.GitHubQueryService;
import com.Hoseo.CapstoneDesign.github.service.GithubAppClientService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.Hoseo.CapstoneDesign.global.aws.properties.SqsProperties;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsBaseMessage;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsMessageSender;
import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushEventStrategy implements GithubWebhookStrategy {

    private final GitHubQueryService gitHubQueryService;
    private final GithubAppClientService githubAppClientService;
    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;
    private final SqsMessageSender sqsMessageSender;
    private final SqsProperties sqsProperties;
    private final AnalysisJobService analysisJobService;

    @Override
    public boolean supports(String eventType) {
        return "push".equals(eventType);
    }

    @Override
    public void handle(JsonNode payload, String deliveryId) {
        if (analysisJobService.existsByDeliveryId(deliveryId))
            return;

        JsonNode installationIdNode = payload.path("installation").path("id");
        JsonNode repositoryIdNode = payload.path("repository").path("id");
        JsonNode repositoryFullNameNode = payload.path("repository").path("full_name");
        JsonNode refNode = payload.path("ref");
        JsonNode beforeNode = payload.path("before");
        JsonNode afterNode = payload.path("after");
        JsonNode senderIdNode = payload.path("sender").path("id");


        if (installationIdNode.isMissingNode() || installationIdNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        if (repositoryIdNode.isMissingNode() || repositoryIdNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        if (repositoryFullNameNode.isMissingNode() || repositoryFullNameNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        if (refNode.isMissingNode() || refNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        if (beforeNode.isMissingNode() || beforeNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        if (afterNode.isMissingNode() || afterNode.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }

        long installationId = installationIdNode.asLong();
        long repositoryId = repositoryIdNode.asLong();
        String ref = refNode.asText();
        String before = beforeNode.asText();
        String after = afterNode.asText();
        String branchName = extractBranchName(ref);
        Long senderId = senderIdNode.isMissingNode() || senderIdNode.isNull()
                ? null : senderIdNode.asLong();

        GitHubWebhookValidationQueryResult validateResult
                =  gitHubQueryService.validateWebhook(installationId,repositoryId,senderId);
        if (validateResult == null) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }
        //깃헙내 콜라보레이터인지 확인
        if ( !githubAppClientService.isRepositoryCollaborator(
                validateResult.matchedInstallationId(),
                validateResult.repositoryFullName(),
                validateResult.installationAccountLogin()))
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);

        String trackedBranch = validateResult.trackedBranch();
        if (!trackedBranch.equals(branchName)) {
            return;
        }

        AnalysisJob job = AnalysisJobEntityFactory.toAnalysisJob(
                gitHubAppInstallationService.getReferenceById(validateResult.matchedInstallationId()),
                installationRepositoryService.getReferenceById(validateResult.installationRepositoryId()),
                after,
                before,
                branchName,
                deliveryId
        );

        AnalysisJob savedJob;
        try {
            savedJob = analysisJobService.create(job);
        } catch (DuplicateKeyException e) {
            return;
        }
        // ex) SQS 메시지 생성, clone 대상 식별 등
        SqsBaseMessage message
                = AnalysisDtoFactory.toSqsAnalysisQueueMessage(savedJob, validateResult);
        sqsMessageSender.send(sqsProperties.analysisQueue(), message);
        savedJob.updateJobStatus(AnalysisJobStatus.ANALYSIS_JOB_QUEUED);
        analysisJobService.create(savedJob);
    }

    private String extractBranchName(String ref) {
        final String prefix = "refs/heads/";
        if (!ref.startsWith(prefix)) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        return ref.substring(prefix.length());
    }

}
