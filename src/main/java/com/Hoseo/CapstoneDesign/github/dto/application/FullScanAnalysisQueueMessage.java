package com.Hoseo.CapstoneDesign.github.dto.application;

public record FullScanAnalysisQueueMessage (
    String repositoryFullName,
    String branchName,
    Long repositoryId,
    Long installationId,
    boolean isPrivate,
    long projectId
) {}
