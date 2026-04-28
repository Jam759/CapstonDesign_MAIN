package com.Hoseo.CapstoneDesign.gamification.facade;

import com.Hoseo.CapstoneDesign.gamification.dto.response.QuestResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.RankingResponse;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;

import java.util.List;

public interface GamificationFacade {
    List<RankingResponse> getRanking(Integer page, Integer size);

    RankingResponse getMyRank(AuthenticatedUserCacheEntry user);

    List<QuestResponse> getMyQuest(
            AuthenticatedUserCacheEntry user,
            Long projectId,
            AiQuestProgressStatus progressStatus,
            String status,
            Integer page,
            Integer size
    );

    QuestResponse acceptQuest(AuthenticatedUserCacheEntry user, Long questId);

    QuestResponse declineQuest(AuthenticatedUserCacheEntry user, Long questId);
}
