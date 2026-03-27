package com.Hoseo.CapstoneDesign.github.service.strategy.impl;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisDtoFactory;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisJobEntityFactory;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.github.dto.application.AnalysisQueueMessage;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.entity.UserGitHubInstallations;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.GithubAppClientService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.UserGitHubInstallationService;
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


//리팩터링 1순위
@Slf4j
@Component
@RequiredArgsConstructor
public class PushEventStrategy implements GithubWebhookStrategy {

    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final GithubAppClientService githubAppClientService;
    private final UserGitHubInstallationService userGitHubInstallationsService;
    private final InstallationRepositoryService installationRepositoryService;
    private final SqsMessageSender sqsMessageSender;
    private final SqsProperties sqsProperties;
    private final AnalysisJobService analysisJobService;
    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

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
        JsonNode senderLoginNode = payload.path("sender").path("login");

        JsonNode pusherNameNode = payload.path("pusher").path("name");
        JsonNode pusherEmailNode = payload.path("pusher").path("email");

        JsonNode pusherTypeNode = payload.path("pusher_type");

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
        String repositoryFullName = repositoryFullNameNode.asText();
        String ref = refNode.asText();
        String before = beforeNode.asText();
        String after = afterNode.asText();
        String branchName = extractBranchName(ref);
        Long senderId = senderIdNode.isMissingNode() || senderIdNode.isNull()
                ? null : senderIdNode.asLong();
        String senderLogin = senderLoginNode.isMissingNode() || senderLoginNode.isNull()
                ? null : senderLoginNode.asText();

        String pusherName = pusherNameNode.isMissingNode() || pusherNameNode.isNull()
                ? null : pusherNameNode.asText();

        String pusherEmail = pusherEmailNode.isMissingNode() || pusherEmailNode.isNull()
                ? null : pusherEmailNode.asText();

        String pusherType = pusherTypeNode.isMissingNode() || pusherTypeNode.isNull()
                ? null : pusherTypeNode.asText();

        GithubAppInstallations installation
                = gitHubAppInstallationService.getById(installationId);
        InstallationRepository installationRepository
                = installationRepositoryService.getByInstallationAndRepositoryId(installation, repositoryId);
        Projects project
                = projectService.getByInstallationRepository(installationRepository);

        //깃헙내 콜라보레이터인지 확인
        if ( !githubAppClientService.isRepositoryCollaborator(
                installationId,repositoryFullName,installation.getAccountLogin()))
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);

        //우리 서비스내 팀원인지 확인 -> 위 로직은 나중에 이용자를 개인이 아닌 깃헙내 organization까지 지원할 경우 바꿔야함
        List<ProjectMember> projectMemberList = projectMemberService.getProjectMember(project);
        Users matchedUser = projectMemberList.stream()
                .map(ProjectMember::getUser)
                .filter(user -> user.getOauthProviderId() != null)
                .filter(user -> user.getOauthProviderId().equals(String.valueOf(senderId)))
                .findFirst()
                .orElseThrow(() -> new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID));

        List<GithubAppInstallations> memberInstallations =
                gitHubAppInstallationService.getAllByUserIds(List.of(matchedUser.getUserId()));

        GithubAppInstallations matchedInstallation = memberInstallations.stream()
                .filter(gi -> gi.getAccountId().equals(senderId))
                .findFirst()
                .orElseThrow(() -> new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID));


        String trackedBranch = project.getTrackedBranch();
        if (!trackedBranch.equals(branchName)) {
            return;
        }

        AnalysisJob job = AnalysisJobEntityFactory.toAnalysisJob(
                matchedInstallation,
                installationRepository,
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
                = AnalysisDtoFactory.toSqsAnalysisQueueMessage(matchedInstallation, installationRepository, savedJob, repositoryFullName, project,matchedUser);
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
