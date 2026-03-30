package com.Hoseo.CapstoneDesign.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 목록용 썸네일 정보")
public class ProjectThumbnailResponse {
    @Schema(description = "프로젝트 ID", example = "101")
    private Long projectId;

    @Schema(description = "프로젝트 제목", example = "알고리즘 스터디")
    private String title;

    @Schema(description = "프로젝트 한 줄 설명", example = "백준 풀이 기록과 회고를 관리하는 프로젝트")
    private String description;
}
