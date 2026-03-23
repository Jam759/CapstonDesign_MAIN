package com.Hoseo.CapstoneDesign.github.facade;

import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.github.dto.application.GithubBranchDto;
import com.Hoseo.CapstoneDesign.github.dto.application.GithubInstallationDetailResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.InstallationsAvailableResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryBranchesResponse;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.entity.UserGitHubInstallations;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.factory.GitHubDtoFactory;
import com.Hoseo.CapstoneDesign.github.factory.GitHubEntityFactory;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.GithubAppClientService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.github.service.UserGitHubInstallationService;
import com.Hoseo.CapstoneDesign.github.service.strategy.GithubWebhookStrategy;
import com.Hoseo.CapstoneDesign.github.util.StateUtil;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Facade
@RequiredArgsConstructor
public class GitHubFacadeImpl implements GitHubFacade {

    private final List<GithubWebhookStrategy> strategies;

    private final UserGitHubInstallationService userGitHubInstallationService;
    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;
    private final GithubAppClientService githubAppClientService;
    private final AnalysisJobService analysisJobService;
    private final UserService userService;
    private final StateUtil stateUtil;

    @Override
    @Transactional(readOnly = true)
    public InstallationsAvailableResponse getAvailable(Users user, String returnTo) {
        Optional<UserGitHubInstallations> appUsers =
                gitHubAppInstallationService.findByUser(user);
        return GitHubDtoFactory.toInstallationsAvailableResponse(user, appUsers, stateUtil, returnTo);
    }

    @Override
    @Transactional(readOnly = false)
    public URI connectInstallationIdAndUser(String state, Long installationId, String setupAction) {
        if ("update".equalsIgnoreCase(setupAction)) {
            if (state == null || state.isBlank()) {
                return stateUtil.buildDefaultRedirectUri();
            }
            return stateUtil.buildRedirectUri(state);
        }

        if (state == null || state.isBlank()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }

        UUID userIdentityId = stateUtil.verifyAndExtractStateId(state);
        Users user = userService.getByIdentityId(userIdentityId);
        GithubInstallationDetailResponse detail =
                githubAppClientService.getInstallationDetail(installationId);
        GithubAppInstallations githubAppInstallations = gitHubAppInstallationService.createOrRefresh(
                installationId,
                detail.account().id(),
                detail.account().login()
        );
        UserGitHubInstallations userGitHubInstallation = GitHubEntityFactory.toUserGitHubInstallations(user, githubAppInstallations);
        userGitHubInstallationService.save(userGitHubInstallation);

        return stateUtil.buildRedirectUri(state);
    }

    @Override
    @Transactional(readOnly = false)
    public void webhookEvent(String event, String deliveryId, String signature256, JsonNode payload) {
        GithubWebhookStrategy strategy = strategies.stream()
                .filter(s -> s.supports(event))
                .findFirst()
                .orElse(null);
        if (strategy == null) {
            return;
        }
        strategy.handle(payload, deliveryId);
    }

    @Override
    @Transactional(readOnly = true)
    public RepositoryBranchesResponse getBranches(Users user, Long installationId, Long repositoryId) {
        GithubAppInstallations installation = gitHubAppInstallationService.getById(installationId);
        InstallationRepository repository =
                installationRepositoryService.getByInstallationAndRepositoryId(installation, repositoryId);

        if (!userGitHubInstallationService.isExistByUserAndInstallation(user, installation))
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);

        List<GithubBranchDto> branches
                = githubAppClientService.getBranches(installationId, repository.getFullName());

        return GitHubDtoFactory.toRepositoryBranchesResponse(installationId, repository, branches);
    }


}
