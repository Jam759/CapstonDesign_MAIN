package com.Hoseo.CapstoneDesign.project.factory;

import com.Hoseo.CapstoneDesign.project.dto.response.ProjectCreateResponse;
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
        String trackedBranch = null;

        if (p.getInstallationRepository() != null) {
            installationRepositoryId = p.getInstallationRepository().getInstallationRepositoryId();
            repositoryFullName = p.getInstallationRepository().getFullName();
        }

        if (p.getGithubAppInstallations() != null) {
            gitHubAppInstallationId = p.getGithubAppInstallations().getGithubAppInstallationsId();
        }

        if (p.getTrackedBranch() != null) {
            trackedBranch = p.getTrackedBranch();
        }

        return ProjectSettingResponse.builder()
                .projectId(p.getProjectId())
                .trackedBranch(trackedBranch)
                .installationRepositoryId(installationRepositoryId)
                .projectStatus(p.getProjectStatus())
                .gitHubAppInstallationId(gitHubAppInstallationId)
                .repositoryFullName(repositoryFullName)
                .build();
    }

    public static ProjectCreateResponse toProjectCreateResponse(Projects saved) {
        return ProjectCreateResponse.builder()
                .projectId(saved.getProjectId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
