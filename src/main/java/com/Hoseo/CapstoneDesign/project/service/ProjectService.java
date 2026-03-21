package com.Hoseo.CapstoneDesign.project.service;

import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.repository.ProjectsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectsRepository repository;

    public Projects create(Projects pjEntity) {
        return repository.save(pjEntity);
    }

    public Projects getById(Long projectId) {
        return repository.findById(projectId)
                .orElseThrow( () -> new ProjectsException(ProjectsErrorCode.PROJECT_NOT_FOUND));
    }

    public Projects getByInstallationRepository(InstallationRepository installationRepository) {
        return repository.findByInstallationRepository(installationRepository)
                .orElseThrow( () -> new ProjectsException(ProjectsErrorCode.PROJECT_NOT_FOUND));
    }
}
