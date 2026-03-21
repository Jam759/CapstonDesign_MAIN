package com.Hoseo.CapstoneDesign.github.repository;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.UserGitHubInstallations;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGitHubInstallationRepository extends JpaRepository<UserGitHubInstallations, Long> {

    Optional<UserGitHubInstallations> findByUser(Users user);

    boolean existsByUserAndGithubAppInstallation(Users user, GithubAppInstallations installation);

    void deleteByGithubAppInstallation(GithubAppInstallations installation);
}
