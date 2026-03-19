package com.Hoseo.CapstoneDesign.project.dto.request;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectType;

public record ProjectCreateRequest(
        String repoUrl,
        ProjectType projectType,
        String projectTitle,
        String description
) {}
