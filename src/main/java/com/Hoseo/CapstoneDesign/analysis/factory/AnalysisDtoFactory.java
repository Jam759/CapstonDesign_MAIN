package com.Hoseo.CapstoneDesign.analysis.factory;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.github.dto.application.AnalysisQueueMessage;
import com.Hoseo.CapstoneDesign.github.dto.application.FullScanAnalysisQueueMessage;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsBaseMessage;
import com.Hoseo.CapstoneDesign.project.entity.Projects;

public class AnalysisDtoFactory {

    public static SqsBaseMessage toSqsAnalysisQueueMessage(
            GithubAppInstallations installations,
            InstallationRepository installationRepository,
            AnalysisJob savedJob,
            String repositoryFullName,
            Projects project) {
        AnalysisQueueMessage message = new AnalysisQueueMessage(
                installations.getGithubAppInstallationsId(),
                installationRepository.getInstallationRepositoryId(),
                repositoryFullName,
                savedJob.getBeforeCommitHash(),
                savedJob.getAfterCommitHash(),
                savedJob.getBranch(),
                installationRepository.isPrivate(),
                project.getProjectId()
        );

        return SqsBaseMessage.builder()
                .jobId(savedJob.getAnalysisJobId().toString())
                .type("NORMAL_ANALYSIS_REQUEST")
                .data(message)
                .build();
    }

    public static SqsBaseMessage toSqsFullScanAnalysisQueueMessage(GithubAppInstallations githubAppInstallations, InstallationRepository repository, AnalysisJob analysisJob, Projects project) {
        FullScanAnalysisQueueMessage message = new FullScanAnalysisQueueMessage(
                repository.getFullName(),
                project.getTrackedBranch(),
                repository.getInstallationRepositoryId(),
                githubAppInstallations.getGithubAppInstallationsId(),
                repository.isPrivate(),
                project.getProjectId()
        );

        return SqsBaseMessage.builder()
                .jobId(analysisJob.getAnalysisJobId().toString())
                .type("FULL_SCAN_ANALYSIS_REQUEST")
                .data(message)
                .build();
    }
}
