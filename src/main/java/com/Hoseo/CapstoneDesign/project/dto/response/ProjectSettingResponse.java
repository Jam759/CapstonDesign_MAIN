package com.Hoseo.CapstoneDesign.project.dto.response;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectStatus;
import lombok.Builder;

@Builder
public record ProjectSettingResponse(
        Long projectId,
        Long gitHubAppInstallationId,
        Long installationRepositoryId,
        String repositoryFullName,
        ProjectStatus projectStatus
) {}
