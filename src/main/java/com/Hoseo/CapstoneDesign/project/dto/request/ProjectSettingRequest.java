package com.Hoseo.CapstoneDesign.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 GitHub 연동 설정 요청")
public record ProjectSettingRequest(
        @Schema(
                description = "/api/v1/github/repositories 응답에서 받은 repositoryId 값",
                example = "3001"
        )
        Long installationRepositoryId,
        @Schema(description = "추적할 브랜치 이름", example = "main")
        String trackedBranch
) {}
