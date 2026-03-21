package com.Hoseo.CapstoneDesign.github.factory;

import com.Hoseo.CapstoneDesign.github.dto.application.GithubBranchDto;
import com.Hoseo.CapstoneDesign.github.dto.response.InstallationsAvailableResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryBranchesResponse;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.entity.UserGitHubInstallations;
import com.Hoseo.CapstoneDesign.github.util.StateUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class GitHubDtoFactory {


    public static InstallationsAvailableResponse toInstallationsAvailableResponse(
            Users user,
            Optional<UserGitHubInstallations> appUsers,
            StateUtil stateUtil,
            String returnTo
    ) {
        String installUrl = "";
        if(appUsers.isEmpty()) {
            String state =
                    stateUtil.createState(user.getIdentityId(),returnTo);
            installUrl =
                    "https://github.com/apps/projectERPERP/installations/new?state="
                            + URLEncoder.encode(state, StandardCharsets.UTF_8);

        }

        return InstallationsAvailableResponse.builder()
                .installed(appUsers.isPresent())
                .installUrl(installUrl)
                .build();
    }

    public static RepositoryBranchesResponse toRepositoryBranchesResponse(
            Long installationId,
            InstallationRepository repository,
            List<GithubBranchDto> branches
    ) {
        List<RepositoryBranchesResponse.BranchItem>  branchItemList =
                branches.stream()
                        .map( dto -> {
                            return RepositoryBranchesResponse.BranchItem.builder()
                                    .protectedBranch(dto.protectedBranch())
                                    .name(dto.name())
                                    .build();
                        }).toList();
        return RepositoryBranchesResponse.builder()
                .repositoryId(repository.getInstallationRepositoryId())
                .repositoryFullName(repository.getFullName())
                .installationId(installationId)
                .branches(branchItemList)
                .build();
    }
}
