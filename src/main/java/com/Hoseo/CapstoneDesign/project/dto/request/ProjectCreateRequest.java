package com.Hoseo.CapstoneDesign.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "프로젝트 생성 요청")
public record ProjectCreateRequest(

        @Schema(description = "프로젝트 제목", example = "캡스톤 디자인")
        String projectTitle,

        @Schema(description = "프로젝트 설명", example = "GitHub 분석 기반 협업 보조 서비스 메인 프로젝트")
        String description,

        String goal, //목표

        LocalDateTime startDate, // 프로젝트 시작 시간

        LocalDateTime endDate, // 프로젝트 마감 시간

        List<String> useTechStackCmdList, //사용 기술 스택 -> 공통 코드 확인

        List<Long> inviteMemberIdList
) {
}
