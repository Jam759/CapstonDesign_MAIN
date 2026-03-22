package com.Hoseo.CapstoneDesign.analysis.factory;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;

public class AnalysisJobEntityFactory {

    public static AnalysisJob toAnalysisJob(
            GithubAppInstallations githubAppInstallation,
            InstallationRepository installationRepository,
            String afterCommit,
            String beforeCommit,
            String branch,
            String deliveryId) {
        return AnalysisJob.builder()
                .githubAppInstallation(githubAppInstallation)
                .installationRepository(installationRepository)
                .jobStatus(AnalysisJobStatus.PENDING)
                .branch(branch)
                .processedAt(null)
                .retryCount((short) 0)
                .beforeCommitHash(beforeCommit)
                .afterCommitHash(afterCommit)
                .deliveryId(deliveryId)
                .build();
    }
}
