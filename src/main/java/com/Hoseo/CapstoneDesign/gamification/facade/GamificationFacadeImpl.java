package com.Hoseo.CapstoneDesign.gamification.facade;

import com.Hoseo.CapstoneDesign.gamification.dto.response.QuestResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.RankingResponse;
import com.Hoseo.CapstoneDesign.gamification.entity.UserAiQuest;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestApprovalStatus;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import com.Hoseo.CapstoneDesign.gamification.factory.GamificationDtoFactory;
import com.Hoseo.CapstoneDesign.gamification.service.UserAiQuestService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserMetaInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class GamificationFacadeImpl implements GamificationFacade {

    private final UserAiQuestService questService;
    private final UserMetaInformationService metaService;

    @Override
    @Transactional(readOnly = true)
    public List<RankingResponse> getRanking(Integer page, Integer size) {
        List<UserMetaInformation> sorted = metaService.getAllMetaInfoByExpDesc();
        return paginate(GamificationDtoFactory.toRankingList(sorted), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public RankingResponse getMyRank(Users user) {
        UserMetaInformation meta = metaService.getMetaInfo(user);
        int rank = (int) metaService.countUsersAbove(meta.getTotalExp()) + 1;
        return GamificationDtoFactory.toRankingResponse(rank, meta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestResponse> getMyQuest(
            Users user,
            Long projectId,
            AiQuestProgressStatus progressStatus,
            String status,
            Integer page,
            Integer size
    ) {
        List<QuestResponse> quests = questService.getQuestsByUserAndProject(user, projectId).stream()
                .filter(q -> progressStatus == null || q.getProgressStatus() == progressStatus)
                .filter(q -> status == null || status.isBlank()
                        || GamificationDtoFactory.toFrontendStatus(q.getApprovalStatus()).equalsIgnoreCase(status))
                .map(GamificationDtoFactory::toQuestResponse)
                .toList();
        return paginate(quests, page, size);
    }

    @Override
    @Transactional(readOnly = false)
    public QuestResponse acceptQuest(Users user, Long questId) {
        UserAiQuest quest = questService.updateQuestStatus(
                questId, AiQuestApprovalStatus.REQUEST_ACCEPT, AiQuestProgressStatus.ACTIVE
        );
        return GamificationDtoFactory.toQuestResponse(quest);
    }

    @Override
    @Transactional(readOnly = false)
    public QuestResponse declineQuest(Users user, Long questId) {
        UserAiQuest quest = questService.updateQuestStatus(
                questId, AiQuestApprovalStatus.REQUEST_REJECT, AiQuestProgressStatus.ARCHIVED
        );
        return GamificationDtoFactory.toQuestResponse(quest);
    }

    private <T> List<T> paginate(List<T> values, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? values.size() : size;
        int fromIndex = Math.min((safePage - 1) * safeSize, values.size());
        int toIndex   = Math.min(fromIndex + safeSize, values.size());
        return values.subList(fromIndex, toIndex);
    }
}
