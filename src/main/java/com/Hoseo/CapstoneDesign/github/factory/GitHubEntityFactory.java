package com.Hoseo.CapstoneDesign.github.factory;

import com.Hoseo.CapstoneDesign.github.dto.application.GithubInstallationDetailResponse;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.user.entity.Users;

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

}
