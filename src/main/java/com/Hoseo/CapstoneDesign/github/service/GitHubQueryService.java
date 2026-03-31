package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.github.dto.query.GitHubWebhookValidationQueryResult;
import com.Hoseo.CapstoneDesign.github.dto.query.UserGitHubInstallationLinkQueryResult;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.mapper.GitHubMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//mapper조회용 service 다른 서비스와 안합치는 이유는 엔티티 중심 로직이라 따로 분리
@Service
@RequiredArgsConstructor
public class GitHubQueryService {

    private final GitHubMapper mapper;

    /**
     * 사용자와 installationRepository 간의 실제 연결 관계를 검증하고 결과를 반환한다.
     * 아래 조건을 모두 만족해야 조회된다.
     *
     * 1. user_git_hub_installations 에 사용자-설치 매핑이 존재할 것
     * 2. github_app_installations 에 해당 설치 정보가 실제 존재할 것
     * 3. installation_repository 에 해당 설치와 연결된 저장소가 존재할 것
     * 4. 조회 대상 installationRepositoryId 와 일치할 것
     *
     * 조건을 만족하지 않으면 GitHubException 을 발생시킨다.
     */
    public UserGitHubInstallationLinkQueryResult getUserLinkedRepoOrThrow(
            Long userId,
            Long installationRepositoryId
    ){
        UserGitHubInstallationLinkQueryResult result =
        mapper.findUserLinkedInstallationRepository(userId, installationRepositoryId);
        if (result == null) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }
        return result;
    }

    public GitHubWebhookValidationQueryResult validateWebhook(
            Long installationId,
            Long installationRepositoryId,
            Long senderAccountId
    ) {
        GitHubWebhookValidationQueryResult result =
                mapper.findWebhookValidation(
                        installationId,
                        installationRepositoryId,
                        senderAccountId
                );

        if (result == null) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }

        return result;
    }

}
