package com.Hoseo.CapstoneDesign.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 생성 결과")
public class ProjectCreateResponse {
    @Schema(description = "생성된 프로젝트 ID", example = "101")
    private Long projectId;

    @Schema(description = "프로젝트 제목", example = "캡스톤 디자인")
    private String title;

    @Schema(description = "프로젝트 설명", example = "GitHub 분석 기반 협업 보조 서비스 메인 프로젝트")
    private String description;

    @Schema(description = "프로젝트 생성 시각", example = "2026-03-30T10:15:00")
    private LocalDateTime createdAt;
}
