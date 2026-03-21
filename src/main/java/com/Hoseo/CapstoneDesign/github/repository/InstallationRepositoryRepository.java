package com.Hoseo.CapstoneDesign.github.repository;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstallationRepositoryRepository extends JpaRepository<InstallationRepository, Long> {
    void deleteAllByGithubAppInstallation(GithubAppInstallations installation);

    void deleteAllByGithubAppInstallationAndInstallationRepositoryIdIn(GithubAppInstallations installation, List<Long> repositoryIds);
}
