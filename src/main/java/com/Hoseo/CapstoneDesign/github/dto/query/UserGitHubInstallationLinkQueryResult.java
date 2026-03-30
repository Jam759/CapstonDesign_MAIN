package com.Hoseo.CapstoneDesign.github.dto.query;

public record UserGitHubInstallationLinkQueryResult(
        Long userId,
        Long gitHubInstallationId,
        Long installationRepositoryId,
        String repositoryFullName
) {
}
