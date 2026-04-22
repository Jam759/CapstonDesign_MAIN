package com.Hoseo.CapstoneDesign.analysis.dto.application;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.PhaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 로드맵 페이즈")
public class ProjectPhaseDto {
    @Schema(description = "페이즈 ID", example = "1")
    private Long id;

    @Schema(description = "페이즈 순서", example = "1")
    private Integer phaseOrder;

    @Schema(description = "페이즈 이름", example = "MVP 구축")
    private String phaseName;

    @Schema(description = "페이즈 목표", example = "핵심 기능을 동작 가능한 형태로 구현합니다.")
    private String phaseObjective;

    @Schema(description = "페이즈 완료 시 기대 산출물", example = "사용자가 핵심 플로우를 끝까지 수행할 수 있습니다.")
    private String phaseOutcome;

    @Schema(description = "페이즈 범위 목록", example = "[\"인증\", \"프로젝트 생성\", \"기본 분석 조회\"]")
    private List<String> phaseScope;

    @Schema(description = "페이즈 종료 기준", example = "핵심 API와 화면 플로우가 통합 테스트를 통과합니다.")
    private String exitCriteria;

    @Schema(description = "페이즈 상태", example = "IN_PROGRESS")
    private PhaseStatus status;
}
