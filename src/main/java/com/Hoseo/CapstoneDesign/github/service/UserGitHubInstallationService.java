package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.UserGitHubInstallations;
import com.Hoseo.CapstoneDesign.github.repository.UserGitHubInstallationRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserGitHubInstallationService {

    private final UserGitHubInstallationRepository repository;

    public boolean isExistByUserAndInstallation(Users user, GithubAppInstallations installation) {
        return repository.existsByUserAndGithubAppInstallation(user, installation);
    }

    public UserGitHubInstallations save(UserGitHubInstallations userGitHubInstallation) {
        return repository.save(userGitHubInstallation);
    }

    public void deleteByGithubAppInstallation(GithubAppInstallations installation) {
        repository.deleteByGithubAppInstallation(installation);
    }

}
