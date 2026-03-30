package com.Hoseo.CapstoneDesign.gamification.dto.response;

import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestApprovalStatus;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 퀘스트 응답")
public class QuestResponse {

    @Schema(description = "현재 퀘스트 진행 상태", example = "ACTIVE")
    private AiQuestProgressStatus progressStatus;

    @Schema(description = "퀘스트 승인 상태", example = "REQUEST_PENDING")
    private AiQuestApprovalStatus approvalStatus;

    @Schema(description = "퀘스트 제목", example = "README 개선")
    private String title;

    @Schema(description = "퀘스트 설명", example = "프로젝트 README에 실행 방법과 구조 설명을 보강하세요.")
    private String description;

    @Schema(description = "퀘스트 수행 힌트", example = "설치, 실행, 주요 패키지 설명 순서로 정리하면 됩니다.")
    private String hint;

    @Schema(description = "AI가 이 퀘스트를 생성한 이유", example = "최근 커밋에서 신규 온보딩 정보가 부족하게 감지되었습니다.")
    private String aiGenerationReason;

    @Schema(description = "퀘스트 보상 경험치", example = "120")
    private Short rewardExp;

}
