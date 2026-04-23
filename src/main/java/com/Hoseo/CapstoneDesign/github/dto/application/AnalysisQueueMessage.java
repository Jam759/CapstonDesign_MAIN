package com.Hoseo.CapstoneDesign.github.dto.application;

public record AnalysisQueueMessage(
        Long jobId,
        long pushUserInstallationId,
        long repositoryId,
        String repositoryFullName,
        String beforeCommit,
        String afterCommit,
        String branchName,
        boolean isPrivate,
        long projectId,
        boolean isMerge
) {
}
