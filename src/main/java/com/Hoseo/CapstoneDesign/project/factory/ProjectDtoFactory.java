package com.Hoseo.CapstoneDesign.project.factory;

import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.entity.Projects;

public class ProjectDtoFactory {
    public static ProjectSettingResponse toProjectSettingResponse(Projects p) {
        if (p == null) {
            return null;
        }

        Long installationRepositoryId = null;
        String repositoryFullName = null;
        Long gitHubAppInstallationId = null;

        if (p.getInstallationRepository() != null) {
            installationRepositoryId = p.getInstallationRepository().getInstallationRepositoryId();
            repositoryFullName = p.getInstallationRepository().getFullName();
        }

        if (p.getGithubAppInstallations() != null) {
            gitHubAppInstallationId = p.getGithubAppInstallations().getGithubAppInstallationsId();
        }

        return ProjectSettingResponse.builder()
                .projectId(p.getProjectId())
                .installationRepositoryId(installationRepositoryId)
                .projectStatus(p.getProjectStatus())
                .gitHubAppInstallationId(gitHubAppInstallationId)
                .repositoryFullName(repositoryFullName)
                .build();
    }
}
