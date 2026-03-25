package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.UserGitHubInstallations;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.repository.GitHubAppInstallationRepository;
import com.Hoseo.CapstoneDesign.github.repository.UserGitHubInstallationRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
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

    public GithubAppInstallations getByUser(Users user) {
        return userGitHubInstallationRepository.findByUser(user)
                .orElseThrow(() -> new GitHubException(GitHubErrorCode.GIT_HUB_NOT_FOUND_USER))
                .getGithubAppInstallation();
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

    public GithubAppInstallations createOrRefresh(
            Long installationId,
            Long accountId,
            String accountLogin
    ) {
        repository.upsertInstallation(installationId, accountId, accountLogin);

        GithubAppInstallations loaded = getById(installationId);

        validateAccount(loaded, accountId);
        return loaded;
    }

    private void validateAccount(GithubAppInstallations installation, Long accountId) {
        if (!installation.getAccountId().equals(accountId)) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }
    }

}
