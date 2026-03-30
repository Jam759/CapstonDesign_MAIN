package com.Hoseo.CapstoneDesign.gamification.dto.response;

import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestApprovalStatus;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestResponse {

    //현재 퀘스트 진행도
    private AiQuestProgressStatus progressStatus;
    //이 퀘스트를 수락 받았는지 안받았는지
    private AiQuestApprovalStatus approvalStatus;

    private String title;

    private String description;

    private String hint;

    private String aiGenerationReason;

    private Short rewardExp;

}
