package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.repository.GitHubAppInstallationRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GitHubAppInstallationService {

    private final GitHubAppInstallationRepository repository;

    public Optional<GithubAppInstallations> findByUser(Users user) {
        return repository.findByUser(user);
    }


    public Optional<GithubAppInstallations> findByid(Long installationId) {
        return repository.findById(installationId);
    }

    public GithubAppInstallations getById(Long installationId) {
        return repository.findById(installationId)
                .orElseThrow( () -> new GitHubException(GitHubErrorCode.GIT_HUB_NOT_FOUND_USER));
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
}
