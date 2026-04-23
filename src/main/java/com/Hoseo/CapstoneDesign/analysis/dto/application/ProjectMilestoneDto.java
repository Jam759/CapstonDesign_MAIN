package com.Hoseo.CapstoneDesign.analysis.dto.application;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.MilestoneStatus;
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
@Schema(description = "프로젝트 로드맵 마일스톤")
public class ProjectMilestoneDto {
    @Schema(description = "마일스톤 ID", example = "10")
    private Long id;

    @Schema(description = "소속 페이즈 ID", example = "1")
    private Long phaseId;

    @Schema(description = "마일스톤 이름", example = "로그인 플로우 완성")
    private String milestoneName;

    @Schema(description = "마일스톤 의도", example = "사용자가 안전하게 서비스에 진입할 수 있게 합니다.")
    private String milestoneIntent;

    @Schema(description = "마일스톤 시작 또는 평가 트리거 조건", example = "OAuth 로그인 API와 콜백 처리가 구현됩니다.")
    private String triggerCondition;

    @Schema(description = "마일스톤 완료 시 기대 상태", example = "신규 사용자가 GitHub 계정으로 로그인할 수 있습니다.")
    private String expectedState;

    @Schema(description = "관찰 가능한 증거 목록", example = "[\"로그인 성공 응답\", \"사용자 세션 생성\", \"인증 실패 케이스 처리\"]")
    private List<String> observableEvidence;

    @Schema(description = "완료 판정 규칙", example = "성공/실패 인증 시나리오 테스트가 모두 통과합니다.")
    private String completionRule;

    @Schema(description = "마일스톤 상태", example = "IN_PROGRESS")
    private MilestoneStatus status;
}
