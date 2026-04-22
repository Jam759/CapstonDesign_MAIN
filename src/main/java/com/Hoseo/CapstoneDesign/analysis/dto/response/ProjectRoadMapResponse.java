package com.Hoseo.CapstoneDesign.analysis.dto.response;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectMilestoneDto;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectPhaseDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "프로젝트 로드맵 응답")
public record ProjectRoadMapResponse(
        @Schema(description = "전체 진행률. 마일스톤이 있으면 달성된 마일스톤 비율, 없으면 완료된 페이즈 비율입니다.", example = "40")
        int overallProgress,
        @Schema(description = "프로젝트 페이즈 목록")
        List<ProjectPhaseDto> phases,
        @Schema(description = "프로젝트 마일스톤 목록")
        List<ProjectMilestoneDto> milestones
) {}
