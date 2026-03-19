package com.Hoseo.CapstoneDesign.github.factory;

import com.Hoseo.CapstoneDesign.github.dto.response.InstallationsAvailableResponse;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.util.StateUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class GitHubDtoFactory {


    public static InstallationsAvailableResponse toInstallationsAvailableResponse(
            Users user,
            Optional<GithubAppInstallations> appUsers,
            StateUtil stateUtil
    ) {
        String installUrl = "";
        if(appUsers.isEmpty()) {
            String state = stateUtil.createState(user.getIdentityId());
            installUrl =
                    "https://github.com/apps/projectERPERP/installations/new?state="
                            + URLEncoder.encode(state, StandardCharsets.UTF_8);

        }

        return InstallationsAvailableResponse.builder()
                .installed(appUsers.isPresent())
                .installUrl(installUrl)
                .build();
    }
}
