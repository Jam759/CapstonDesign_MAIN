package com.Hoseo.CapstoneDesign.project.dto.request;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 생성 요청")
public record ProjectCreateRequest(
        @Schema(description = "프로젝트 유형", example = "PROJECT")
        ProjectType projectType,
        @Schema(description = "프로젝트 제목", example = "캡스톤 디자인")
        String projectTitle,
        @Schema(description = "프로젝트 설명", example = "GitHub 분석 기반 협업 보조 서비스 메인 프로젝트")
        String description
) {}
