package com.Hoseo.CapstoneDesign.project.factory;

import com.Hoseo.CapstoneDesign.project.dto.response.ProjectCreateResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.project.entity.Projects;

import java.util.List;

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
        return toProjectCreateResponse(saved, List.of());
    }

    public static ProjectCreateResponse toProjectCreateResponse(Projects saved, List<Long> invitedMemberIds) {
        Long installationRepositoryId = null;
        String repositoryFullName = null;

        if (saved.getInstallationRepository() != null) {
            installationRepositoryId = saved.getInstallationRepository().getInstallationRepositoryId();
            repositoryFullName = saved.getInstallationRepository().getFullName();
        }

        return ProjectCreateResponse.builder()
                .projectId(saved.getProjectId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .installationRepositoryId(installationRepositoryId)
                .repositoryFullName(repositoryFullName)
                .trackedBranch(saved.getTrackedBranch())
                .invitedMemberIds(invitedMemberIds)
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public static ProjectThumbnailResponse toProjectThumbnailResponse(
            Projects project,
            List<String> techStacks,
            List<String> teamMembers,
            String role
    ) {
        String repositoryFullName = project.getInstallationRepository() != null
                ? project.getInstallationRepository().getFullName()
                : null;

        return ProjectThumbnailResponse.builder()
                .projectId(project.getProjectId())
                .id(project.getProjectId())
                .title(project.getTitle())
                .name(project.getTitle())
                .type(teamMembers.size() > 1 ? "team" : "personal")
                .role(role)
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .techStack(techStacks)
                .stacks(techStacks)
                .githubRepo(repositoryFullName)
                .githubBranch(project.getTrackedBranch())
                .teamMembers(teamMembers)
                .build();
    }
}
