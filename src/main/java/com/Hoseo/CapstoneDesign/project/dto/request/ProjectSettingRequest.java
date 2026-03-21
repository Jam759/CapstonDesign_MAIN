package com.Hoseo.CapstoneDesign.project.dto.request;

public record ProjectSettingRequest(
        Long installationRepositoryId,
        String trackedBranch
) {}
