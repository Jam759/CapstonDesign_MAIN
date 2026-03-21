package com.Hoseo.CapstoneDesign.github.factory;

import com.Hoseo.CapstoneDesign.github.dto.application.GithubInstallationDetailResponse;
import com.Hoseo.CapstoneDesign.github.dto.application.GithubRepositorySummary;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class GitHubEntityFactory {

    public static GithubAppInstallations toGithubAppInstallations(
            Long installationId,
            Users users,
            GithubInstallationDetailResponse detailResponse
    ){
        return GithubAppInstallations.builder()
                .user(users)
                .GithubAppInstallationsId(installationId)
                .accountLogin(detailResponse.account().login())
                .accountId(detailResponse.account().id())
                .build();
    }

    public static InstallationRepository toInstallationRepository(GithubAppInstallations installations,JsonNode repoNode){
        return InstallationRepository.builder()
                .installationRepositoryId(repoNode.path("id").asLong())
                .githubAppInstallation(installations)
                .fullName(repoNode.path("full_name").asText(null))
                .isPrivate(repoNode.path("private").asBoolean(false))
                .name(repoNode.path("name").asText(null))
                .build();
    }

    public static List<InstallationRepository> toInstallationRepositories(
            GithubAppInstallations installations,
            List<GithubRepositorySummary> repoList
    ) {
        if (installations == null) {
            throw new IllegalArgumentException("installations must not be null");
        }
        if (repoList == null || repoList.isEmpty()) {
            return List.of();
        }

        return repoList.stream()
                .map(repo -> InstallationRepository.builder()
                        .githubAppInstallation(installations)
                        .installationRepositoryId(repo.id())
                        .fullName(repo.fullName())
                        .isPrivate(repo.isPrivate())
                        .name(repo.name())
                        .build())
                .toList();
    }

}
