package com.Hoseo.CapstoneDesign.project.dto.response;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "프로젝트 GitHub 연동 설정 조회 결과")
@Builder
public record ProjectSettingResponse(
        @Schema(description = "프로젝트 ID", example = "101")
        Long projectId,
        @Schema(description = "연결된 GitHub App installation 내부 ID", example = "77")
        Long gitHubAppInstallationId,
        @Schema(description = "연결된 설치 저장소 ID", example = "3001")
        Long installationRepositoryId,
        @Schema(description = "추적 중인 브랜치", example = "main")
        String trackedBranch,
        @Schema(description = "저장소 전체 이름", example = "Jam759/CapstoneDesign")
        String repositoryFullName,
        @Schema(description = "프로젝트 저장소 연결 상태", example = "REPO_CONNECTED")
        ProjectStatus projectStatus
) {}
