package com.Hoseo.CapstoneDesign.github.dto.application;

public record PullRequestWebhookContext(
        String action,
        Long installationId,
        Long repositoryId,
        String repositoryFullName,
        Long senderId,
        Long prNumber,
        String baseRef,
        String baseSha,
        String headRef,
        String headSha,
        boolean merged,
        String mergeCommitSha,
        String deliveryId
) {
}
