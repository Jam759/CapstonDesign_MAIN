package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.auth.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.auth.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.dto.application.GithubInstallationDetailResponse;
import com.Hoseo.CapstoneDesign.github.util.GithubJwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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

}
