package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.github.dto.application.GithubInstallationDetailResponse;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.UserGitHubInstallations;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.factory.GitHubEntityFactory;
import com.Hoseo.CapstoneDesign.github.repository.GitHubAppInstallationRepository;
import com.Hoseo.CapstoneDesign.github.repository.UserGitHubInstallationRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GitHubAppInstallationService {

    private final GitHubAppInstallationRepository repository;
    private final UserGitHubInstallationRepository userGitHubInstallationRepository;

    public Optional<UserGitHubInstallations> findByUser(Users user) {

        return userGitHubInstallationRepository.findByUser(user);
    }


    public Optional<GithubAppInstallations> findByid(Long installationId) {
        return repository.findById(installationId);
    }

    public GithubAppInstallations getById(Long installationId) {
        return repository.findById(installationId)
                .orElseThrow(() -> new GitHubException(GitHubErrorCode.GIT_HUB_NOT_FOUND_USER));
    }

    public GithubAppInstallations save(GithubAppInstallations entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_SAVE_ERROR);
        }
    }

    public void delete(GithubAppInstallations installation) {
        repository.delete(installation);
    }


    public GithubAppInstallations createOrRefresh(Long installationId, Long accountId,String accountLogin) {
        return repository.findById(installationId)
                .map(existing -> {
                    if (!existing.getAccountId().equals(accountId)) {
                        throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
                    }
                    existing.refreshFrom(accountId, accountLogin);
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    try {
                        GithubAppInstallations entity =
                                GitHubEntityFactory.toGithubAppInstallations(installationId, accountId, accountLogin);
                        return repository.save(entity);
                    } catch (DataIntegrityViolationException e) {
                        return repository.findById(installationId).orElseThrow();
                        // webhook, setUpCallback에서 비슷한 시각에 같은 pk로 2번 저장하는
                        // race condition남 예외 처리로 흡수
                    }
                });
    }
}
