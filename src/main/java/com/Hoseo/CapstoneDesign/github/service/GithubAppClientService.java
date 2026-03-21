package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.github.dto.application.*;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.util.GithubJwtUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class GithubAppClientService {

    private final RestClient restClient;
    private final GithubJwtUtil githubJwtUtil;

    public GithubAppClientService(GithubJwtUtil githubJwtUtil) {
        this.githubJwtUtil = githubJwtUtil;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }

    public GithubInstallationDetailResponse getInstallationDetail(Long installationId) {
        String appJwt = githubJwtUtil.createAppJwt();

        try {
            return restClient.get()
                    .uri("/app/installations/{installationId}", installationId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + appJwt)
                    .retrieve()
                    .body(GithubInstallationDetailResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_NOT_FOUND_INSTALLATION);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_FORBIDDEN);
        }
    }

    public List<GithubRepositorySummary> getAllAccessibleRepositories(
            String installationToken
    ) {
        List<GithubRepositorySummary> result = new ArrayList<>();
        int page = 1;

        while (true) {
            GithubInstallationRepositoriesResponse response;

            try {
                int finalPage = page;
                response = restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/installation/repositories")
                                .queryParam("per_page", 100)
                                .queryParam("page", finalPage)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + installationToken)
                        .retrieve()
                        .body(GithubInstallationRepositoriesResponse.class);
            } catch (HttpClientErrorException.Unauthorized e) {
                throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_FORBIDDEN);
            } catch (HttpClientErrorException.Forbidden e) {
                throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_FORBIDDEN);
            }

            if (response == null || response.repositories() == null || response.repositories().isEmpty()) {
                break;
            }

            result.addAll(response.repositories());

            if (response.repositories().size() < 100) {
                break;
            }

            page++;
        }

        return result;
    }

    public String createInstallationAccessToken(Long installationId) {
        String appJwt = githubJwtUtil.createAppJwt();

        try {
            GithubInstallationTokenResponse response = restClient.post()
                    .uri("/app/installations/{installationId}/access_tokens", installationId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + appJwt)
                    .retrieve()
                    .body(GithubInstallationTokenResponse.class);

            if (response == null || response.token() == null || response.token().isBlank()) {
                throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_FORBIDDEN);
            }

            return response.token();
        } catch (HttpClientErrorException.NotFound e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_NOT_FOUND_INSTALLATION);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_FORBIDDEN);
        }
    }

    public List<GithubBranchDto> getBranches(Long installationId, String fullName) {
        String token = this.createInstallationAccessToken(installationId);

        String[] parts = fullName.split("/");
        if (parts.length != 2) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }

        String owner = parts[0];
        String repo = parts[1];

        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GithubBranchDto>>() {
                });
    }

}
