package com.Hoseo.CapstoneDesign.github.dto.query;

public record GitHubWebhookValidationQueryResult(
        Long projectId,
        String trackedBranch,
        Long projectMemberId,
        Long userId,
        String oauthProviderId,
        Long matchedInstallationId,
        Long matchedInstallationAccountId,
        Long installationRepositoryId,
        String repositoryFullName,
        Boolean isPrivate,
        String installationAccountLogin
) {
}