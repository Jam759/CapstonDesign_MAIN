package com.Hoseo.CapstoneDesign.github.factory;

import com.Hoseo.CapstoneDesign.github.dto.application.GithubBranchDto;
import com.Hoseo.CapstoneDesign.github.dto.query.UserGitHubInstallationLinkQueryResult;
import com.Hoseo.CapstoneDesign.github.dto.response.InstallationsAvailableResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryBranchesResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryResponse;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.util.StateUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GitHubDtoFactory {

    public static InstallationsAvailableResponse toInstallationsAvailableResponse(
            Users user,
            boolean githubInstalled,
            StateUtil stateUtil,
            String returnTo
    ) {
        String installUrl = "";
        if (!githubInstalled) {
            String state =
                    stateUtil.createState(user.getIdentityId(), returnTo);
            installUrl =
                    "https://github.com/apps/projectlxp/installations/new?state="
                            + URLEncoder.encode(state, StandardCharsets.UTF_8);
        }

        return InstallationsAvailableResponse.builder()
                .installed(githubInstalled)
                .installUrl(installUrl)
                .build();
    }

    public static RepositoryBranchesResponse toRepositoryBranchesResponse(
            UserGitHubInstallationLinkQueryResult result, List<GithubBranchDto> branches
    ) {
        List<RepositoryBranchesResponse.BranchItem> branchItemList =
                branches.stream()
                        .map(dto -> RepositoryBranchesResponse.BranchItem.builder()
                                .protectedBranch(dto.protectedBranch())
                                .name(dto.name())
                                .build())
                        .toList();
        return RepositoryBranchesResponse.builder()
                .repositoryId(result.installationRepositoryId())
                .repositoryFullName(result.repositoryFullName())
                .installationId(result.gitHubInstallationId())
                .branches(branchItemList)
                .build();
    }

    public static RepositoryResponse toRepositoryResponse(InstallationRepository repository) {
        return RepositoryResponse.builder()
                .repositoryId(repository.getInstallationRepositoryId())
                .repositoryFullName(repository.getFullName())
                .isPrivate(repository.isPrivate())
                .build();
    }
}
