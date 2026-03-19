package com.Hoseo.CapstoneDesign.github.factory;

import com.Hoseo.CapstoneDesign.github.dto.application.GithubInstallationDetailResponse;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.fasterxml.jackson.databind.JsonNode;

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
                .GithubAppInstallationsId(installations)
                .fullName(repoNode.path("full_name").asText(null))
                .isPrivate(repoNode.path("private").asBoolean(false))
                .name(repoNode.path("name").asText(null))
                .build();
    }

}
