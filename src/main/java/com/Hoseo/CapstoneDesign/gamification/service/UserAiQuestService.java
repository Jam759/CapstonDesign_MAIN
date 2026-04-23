package com.Hoseo.CapstoneDesign.gamification.service;

import com.Hoseo.CapstoneDesign.gamification.entity.UserAiQuest;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestApprovalStatus;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import com.Hoseo.CapstoneDesign.gamification.exception.GamificationErrorCode;
import com.Hoseo.CapstoneDesign.gamification.exception.GamificationException;
import com.Hoseo.CapstoneDesign.gamification.repository.UserPersonalQuestRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAiQuestService {

    private final UserPersonalQuestRepository repository;

    public List<UserAiQuest> getQuestsByUser(Users user) {
        return repository.findByUser(user);
    }

    public List<UserAiQuest> getQuestsByUserAndProject(Users user, Long projectId) {
        return repository.findByUserAndProject_ProjectId(user, projectId);
    }

    public UserAiQuest updateQuestStatus(
            Long questId,
            AiQuestApprovalStatus approvalStatus,
            AiQuestProgressStatus progressStatus
    ) {
        UserAiQuest quest = repository.findById(questId)
                .orElseThrow(() -> new GamificationException(GamificationErrorCode.QUEST_NOT_FOUND));
        quest.updateStatus(approvalStatus, progressStatus);
        return repository.save(quest);
    }
}
