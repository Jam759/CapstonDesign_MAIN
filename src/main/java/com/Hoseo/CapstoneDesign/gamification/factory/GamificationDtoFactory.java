package com.Hoseo.CapstoneDesign.gamification.factory;

import com.Hoseo.CapstoneDesign.gamification.dto.response.QuestResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.RankingResponse;
import com.Hoseo.CapstoneDesign.gamification.entity.UserAiQuest;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestApprovalStatus;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.List;

public class GamificationDtoFactory {

    public static QuestResponse toQuestResponse(UserAiQuest q) {
        return QuestResponse.builder()
                .id(q.getUserAiQuestId())
                .relatedMilestoneId(q.getRelatedMilestone() != null ? q.getRelatedMilestone().getProjectMilestoneId() : null)
                .title(q.getTitle())
                .description(q.getDescription())
                .category(q.getCategory() != null ? q.getCategory().getCommonGroupDetailId() : null)
                .xp(q.getRewardExp())
                .status(toFrontendStatus(q.getApprovalStatus()))
                .createdBy("AI")
                .expiresAt(q.getExpiredAt())
                .createdAt(q.getCreatedAt())
                .hint(q.getHint())
                .aiGenerationReason(q.getAiGenerationReason())
                .progressStatus(q.getProgressStatus())
                .approvalStatus(q.getApprovalStatus())
                .rewardExp(q.getRewardExp())
                .build();
    }

    public static List<RankingResponse> toRankingList(List<UserMetaInformation> sorted) {
        return sorted.stream()
                .map(GamificationDtoFactory::toRankingResponse)
                .toList();
    }

    public static RankingResponse toRankingResponse(UserMetaInformation meta) {
        Users user = meta.getUser();
        return RankingResponse.builder()
                .rank(meta.getCurrentRank())
                .userId(user.getUserId())
                .serviceNickname(resolveDisplayName(user))
                .level(meta.getCurrentLevel())
                .totalExp(meta.getTotalExp())
                .build();
    }

    public static String toFrontendStatus(AiQuestApprovalStatus approvalStatus) {
        return switch (approvalStatus) {
            case REQUEST_PENDING -> "pending";
            case REQUEST_ACCEPT  -> "accepted";
            case REQUEST_REJECT  -> "declined";
            case CLEARED         -> "completed";
        };
    }

    public static String resolveDisplayName(Users user) {
        if (user == null) return "service-user";
        if (user.getServiceNickname() != null && !user.getServiceNickname().isBlank())
            return user.getServiceNickname();
        if (user.getOauthNickname() != null && !user.getOauthNickname().isBlank())
            return user.getOauthNickname();
        return "service-user";
    }
}
