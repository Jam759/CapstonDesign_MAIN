package com.Hoseo.CapstoneDesign.github.dto.application;

public record AnalysisQueueMessage(
        long installationId,
        long repositoryId,
        String repositoryFullName,
        String beforeCommit,
        String afterCommit,
        String branchName,
        boolean isPrivate,
        long projectId
) {
}
