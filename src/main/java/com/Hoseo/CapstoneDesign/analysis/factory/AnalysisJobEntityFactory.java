package com.Hoseo.CapstoneDesign.analysis.factory;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisEventType;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public class AnalysisJobEntityFactory {

    public static AnalysisJob toAnalysisJob(
            Projects project,
            Users user,
            GithubAppInstallations githubAppInstallation,
            InstallationRepository installationRepository,
            String afterCommit,
            String beforeCommit,
            String branch,
            String deliveryId,
            AnalysisEventType analysisEventType,
            boolean isPrivateRepo
    ) {
        return toAnalysisJob(
                project,
                user,
                githubAppInstallation,
                installationRepository,
                afterCommit,
                beforeCommit,
                branch,
                deliveryId,
                analysisEventType,
                isPrivateRepo,
                false
        );
    }

    public static AnalysisJob toAnalysisJob(
            Projects project,
            Users user,
            GithubAppInstallations githubAppInstallation,
            InstallationRepository installationRepository,
            String afterCommit,
            String beforeCommit,
            String branch,
            String deliveryId,
            AnalysisEventType analysisEventType,
            boolean isPrivateRepo,
            boolean mergeAnalysis) {
        return AnalysisJob.builder()
                .project(project)
                .user(user)
                .githubAppInstallation(githubAppInstallation)
                .installationRepository(installationRepository)
                .jobStatus(AnalysisJobStatus.PENDING)
                .branch(branch)
                .processedAt(null)
                .retryCount((short) 0)
                .beforeCommitHash(beforeCommit)
                .afterCommitHash(afterCommit)
                .deliveryId(deliveryId)
                .analysisEventType(analysisEventType)
                .isPrivateRepo(isPrivateRepo)
                .mergeAnalysis(mergeAnalysis)
                .build();
    }
}
