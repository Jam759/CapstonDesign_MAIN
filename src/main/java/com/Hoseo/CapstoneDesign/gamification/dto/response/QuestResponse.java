package com.Hoseo.CapstoneDesign.gamification.dto.response;

import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestApprovalStatus;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
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
@Schema(description = "AI quest response")
public class QuestResponse {

    @Schema(description = "Quest id", example = "1")
    private Long id;

    @Schema(description = "Related roadmap milestone id", example = "10")
    private Long relatedMilestoneId;

    @Schema(description = "Quest title", example = "Improve README")
    private String title;

    @Schema(description = "Quest description", example = "Add setup and run instructions to README.")
    private String description;

    @Schema(description = "Frontend category", example = "COLLABORATION")
    private String category;

    @Schema(description = "Reward experience", example = "120")
    private Short xp;

    @Schema(description = "Frontend status: pending, accepted, declined, completed", example = "pending")
    private String status;

    @Schema(description = "Quest creator", example = "AI")
    private String createdBy;

    @Schema(description = "Quest expiration datetime", example = "2026-04-30T23:59:59")
    private LocalDateTime expiresAt;

    @Schema(description = "Quest created datetime", example = "2026-04-22T10:15:00")
    private LocalDateTime createdAt;

    @Schema(description = "Execution hint", example = "Start with installation and run instructions.")
    private String hint;

    @Schema(description = "AI generation reason", example = "Recent commits showed missing onboarding docs.")
    private String aiGenerationReason;

    @Schema(description = "Legacy progress status", example = "ACTIVE")
    private AiQuestProgressStatus progressStatus;

    @Schema(description = "Legacy approval status", example = "REQUEST_PENDING")
    private AiQuestApprovalStatus approvalStatus;

    @Schema(description = "Legacy reward experience", example = "120")
    private Short rewardExp;
}
