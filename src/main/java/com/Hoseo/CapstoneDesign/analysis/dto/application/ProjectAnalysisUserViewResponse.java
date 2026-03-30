package com.Hoseo.CapstoneDesign.analysis.dto.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "프로젝트 분석 USER_VIEW 원본 응답")
public record ProjectAnalysisUserViewResponse(
        @Schema(description = "리포트 스키마 버전", example = "1.0.0")
        String schemaVersion,
        @Schema(description = "리포트 생성 시각", example = "2026-03-27T16:45:03.585Z")
        String generatedAt,
        @Schema(description = "분석 대상 범위")
        Scope scope,
        @Schema(description = "한 줄 핵심 요약", example = "최근 커밋 품질이 안정적으로 개선되고 있습니다.")
        String headline,
        @Schema(description = "상세 요약", example = "테스트와 예외 처리 품질은 양호하지만 문서화와 협업 패턴 보강이 필요합니다.")
        String summary,
        @ArraySchema(schema = @Schema(description = "프로젝트 강점", example = "테스트 커버리지가 안정적으로 유지되고 있습니다."))
        List<String> strengths,
        @ArraySchema(schema = @Schema(description = "프로젝트 리스크", example = "README 문서화가 부족해 신규 참여자 온보딩 비용이 큽니다."))
        List<String> risks,
        @ArraySchema(schema = @Schema(implementation = Advice.class))
        List<Advice> advice,
        @Schema(description = "점수표")
        Scorecard scorecard
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "분석 대상 범위 정보")
    public record Scope(
            @Schema(description = "프로젝트 ID", example = "101")
            Long projectId,
            @Schema(description = "조회 사용자 ID", example = "1")
            Long userId,
            @Schema(description = "저장소 전체 이름", example = "Jam759/CapstoneDesign")
            String repositoryFullName,
            @Schema(description = "분석 대상 브랜치", example = "main")
            String branchName,
            @Schema(description = "비교 이전 커밋 해시", example = "before-hash")
            String beforeCommitHash,
            @Schema(description = "비교 이후 커밋 해시", example = "after-hash")
            String afterCommitHash
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "개선 조언 항목")
    public record Advice(
            @Schema(description = "조언 ID", example = "doc-001")
            String id,
            @Schema(description = "우선순위", example = "HIGH")
            String priority,
            @Schema(description = "카테고리", example = "DOCUMENTATION")
            String category,
            @Schema(description = "조언 제목", example = "README 온보딩 정보 보강")
            String title,
            @Schema(description = "조언 본문", example = "신규 참여자가 실행 방법을 빠르게 이해할 수 있도록 README를 보강하세요.")
            String body,
            @Schema(description = "추천 액션", example = "설치, 실행, 환경 변수, 주요 패키지 설명 섹션을 추가하세요.")
            String recommendedAction,
            @Schema(description = "예상 효과", example = "온보딩 시간을 줄이고 협업 진입 장벽을 낮출 수 있습니다.")
            String expectedImpact
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "분석 점수표")
    public record Scorecard(
            @Schema(description = "전체 점수")
            Overall overall,
            @ArraySchema(schema = @Schema(implementation = Category.class))
            List<Category> categories
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "전체 점수 요약")
    public record Overall(
            @Schema(description = "전체 점수", example = "86")
            Integer score,
            @Schema(description = "등급", example = "A")
            String grade,
            @Schema(description = "신뢰도", example = "HIGH")
            String confidence
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "카테고리별 점수")
    public record Category(
            @Schema(description = "카테고리 키", example = "TESTING")
            String key,
            @Schema(description = "카테고리 이름", example = "테스트")
            String label,
            @Schema(description = "카테고리 점수", example = "90")
            Integer score,
            @Schema(description = "점수 산정 이유", example = "회귀 테스트가 주요 컨트롤러 계약을 잘 커버하고 있습니다.")
            String reason,
            @ArraySchema(schema = @Schema(description = "근거 항목", example = "MockMvc 기반 컨트롤러 테스트가 존재합니다."))
            List<String> evidence
    ) {
    }
}
