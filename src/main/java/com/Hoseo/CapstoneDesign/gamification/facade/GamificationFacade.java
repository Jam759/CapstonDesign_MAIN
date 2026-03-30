package com.Hoseo.CapstoneDesign.gamification.facade;

import com.Hoseo.CapstoneDesign.gamification.dto.response.BadgeResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.QuestResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.RankingResponse;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.List;

public interface GamificationFacade {
    List<RankingResponse> getRanking(Integer page, Integer size);

    RankingResponse getMyRank(Users user);

    List<QuestResponse> getMyQuest(Users user, AiQuestProgressStatus progressStatus, Integer page, Integer size);

    List<BadgeResponse> getMyBadges(Users user);
}
