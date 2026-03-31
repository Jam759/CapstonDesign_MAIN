package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.exception.InstallationRepositoryErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.InstallationRepositoryException;
import com.Hoseo.CapstoneDesign.github.repository.InstallationRepositoryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallationRepositoryService {

    private final InstallationRepositoryRepository repository;
    private final EntityManager entityManager;

    public List<InstallationRepository> saveAll(List<InstallationRepository> entities) {
        return repository.saveAll(entities);
    }

    public InstallationRepository getReferenceById(Long id) {
        return repository.getReferenceById(id);
    }

    public void bulkInsert(List<InstallationRepository> entities) {
        for (InstallationRepository entity : entities) {
            entityManager.persist(entity);
        }
    }

    public void deleteAllByInstallation(GithubAppInstallations installation) {
        repository.deleteAllByGithubAppInstallation(installation);
    }

    public void deleteAllByInstallationAndRepositoryIds(
            GithubAppInstallations installation,
            List<Long> repositoryIds
    ) {
        repository.deleteAllByGithubAppInstallationAndInstallationRepositoryIdIn(installation, repositoryIds);
    }

    public InstallationRepository getByInstallationAndRepositoryId(GithubAppInstallations installation, Long repositoryId) {
        return repository.findByGithubAppInstallationAndInstallationRepositoryId(installation, repositoryId)
                .orElseThrow(() -> new InstallationRepositoryException(
                        InstallationRepositoryErrorCode.INSTALLATION_REPOSITORY_NOT_FOUND
                ));
    }

    public List<InstallationRepository> getByGithubAppInstallations(GithubAppInstallations installation) {
        return repository.findAllByGithubAppInstallation(installation);
    }
}
