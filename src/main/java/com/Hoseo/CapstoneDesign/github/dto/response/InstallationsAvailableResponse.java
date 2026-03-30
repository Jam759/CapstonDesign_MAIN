package com.Hoseo.CapstoneDesign.github.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "GitHub App 설치 가능 여부 응답")
@Builder
public record InstallationsAvailableResponse(
        @Schema(description = "현재 사용자가 GitHub App을 이미 연결했는지 여부", example = "false")
        boolean installed,
        @Schema(
                description = "미설치 상태일 때 사용할 GitHub App 설치 URL. installed=true면 빈 문자열일 수 있습니다.",
                example = "https://github.com/apps/projectERPERP/installations/new?state=..."
        )
        String installUrl
) {}
