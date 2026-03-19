package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.auth.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.auth.exception.GitHubException;
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

    public GithubAppInstallations save(GithubAppInstallations entity) {
        try {
             return repository.save(entity);
         } catch (Exception e) {
             throw new GitHubException(GitHubErrorCode.GIT_HUB_SAVE_ERROR);
         }
    }
}
