package com.Hoseo.CapstoneDesign.gamification.facade;

import com.Hoseo.CapstoneDesign.gamification.dto.response.BadgeResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.QuestResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.RankingResponse;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestApprovalStatus;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Facade
public class GamificationFacadeImpl implements GamificationFacade{

    @Override
    @Transactional(readOnly = true)
    public List<RankingResponse> getRanking(Integer page, Integer size) {
        // TODO : 추후 구현 현재는 mock데이터 반환
        return paginate(mockRanking(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public RankingResponse getMyRank(Users user) {
        // TODO : 추후 구현 현재는 mock데이터 반환
        return RankingResponse.builder()
                .rank(7)
                .userId(resolveUserId(user))
                .serviceNickname(resolveDisplayName(user))
                .level(3)
                .totalExp(1280L)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestResponse> getMyQuest(Users user, AiQuestProgressStatus progressStatus, Integer page, Integer size) {
        // TODO : 추후 구현 현재는 mock데이터 반환
        List<QuestResponse> filtered = mockQuests().stream()
                .filter(quest -> progressStatus == null || quest.getProgressStatus() == progressStatus)
                .toList();
        return paginate(filtered, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponse> getMyBadges(Users user) {
        // TODO : 추후 구현 현재는 mock데이터 반환
        return List.of(
                BadgeResponse.builder()
                        .badgeId(501L)
                        .badgeName("첫 분석 완료")
                        .badgeDescription("첫 프로젝트 분석을 완료했습니다.")
                        .badgeImageUrl("https://example.com/badges/first-analysis.png")
                        .badgeType("ANALYSIS")
                        .acquiredAt(LocalDateTime.of(2026, 3, 10, 14, 0))
                        .build(),
                BadgeResponse.builder()
                        .badgeId(502L)
                        .badgeName("협업 기여자")
                        .badgeDescription("프로젝트 초대 응답과 협업 활동을 완료했습니다.")
                        .badgeImageUrl("https://example.com/badges/collaboration.png")
                        .badgeType("COLLABORATION")
                        .acquiredAt(LocalDateTime.of(2026, 3, 24, 9, 30))
                        .build()
        );
    }

    // TODO : 추후 구현 현재는 mock데이터 반환
    private List<RankingResponse> mockRanking() {
        return List.of(
                RankingResponse.builder().rank(1).userId(1001L).serviceNickname("commit-master").level(9).totalExp(4820L).build(),
                RankingResponse.builder().rank(2).userId(1002L).serviceNickname("branch-hunter").level(8).totalExp(4380L).build(),
                RankingResponse.builder().rank(3).userId(1003L).serviceNickname("merge-wizard").level(7).totalExp(4010L).build(),
                RankingResponse.builder().rank(4).userId(1004L).serviceNickname("code-reviewer").level(6).totalExp(3560L).build(),
                RankingResponse.builder().rank(5).userId(1005L).serviceNickname("daily-pusher").level(5).totalExp(3200L).build(),
                RankingResponse.builder().rank(6).userId(1006L).serviceNickname("issue-tamer").level(4).totalExp(2710L).build(),
                RankingResponse.builder().rank(7).userId(1007L).serviceNickname("service-user").level(3).totalExp(1280L).build(),
                RankingResponse.builder().rank(8).userId(1008L).serviceNickname("test-runner").level(3).totalExp(1190L).build()
        );
    }

    // TODO : 추후 구현 현재는 mock데이터 반환
    private List<QuestResponse> mockQuests() {
        return List.of(
                QuestResponse.builder()
                        .progressStatus(AiQuestProgressStatus.ACTIVE)
                        .approvalStatus(AiQuestApprovalStatus.REQUEST_PENDING)
                        .title("README 개선")
                        .description("프로젝트 README에 실행 방법과 구조 설명을 보강하세요.")
                        .hint("설치, 실행, 주요 패키지 설명 순서로 정리하면 됩니다.")
                        .aiGenerationReason("최근 커밋에서 신규 온보딩 정보가 부족하게 감지되었습니다.")
                        .rewardExp((short) 120)
                        .build(),
                QuestResponse.builder()
                        .progressStatus(AiQuestProgressStatus.COMPLETED)
                        .approvalStatus(AiQuestApprovalStatus.CLEARED)
                        .title("예외 처리 정리")
                        .description("GlobalExceptionHandler에 누락된 예외 응답 포맷을 맞추세요.")
                        .hint("도메인별 커스텀 예외를 하나의 응답 구조로 통일하면 됩니다.")
                        .aiGenerationReason("최근 분석에서 예외 응답 스키마 불일치가 발견되었습니다.")
                        .rewardExp((short) 180)
                        .build(),
                QuestResponse.builder()
                        .progressStatus(AiQuestProgressStatus.EXPIRED)
                        .approvalStatus(AiQuestApprovalStatus.REQUEST_REJECT)
                        .title("테스트 커버리지 보강")
                        .description("프로젝트 컨트롤러 contract 테스트를 추가하세요.")
                        .hint("MockMvc 기반으로 200 응답과 필드 매핑을 확인하면 됩니다.")
                        .aiGenerationReason("컨트롤러 주요 경로에 회귀 방지 테스트가 부족했습니다.")
                        .rewardExp((short) 150)
                        .build()
        );
    }

    private String resolveDisplayName(Users user) {
        if (user == null) {
            return "service-user";
        }
        if (user.getServiceNickname() != null && !user.getServiceNickname().isBlank()) {
            return user.getServiceNickname();
        }
        if (user.getOauthNickname() != null && !user.getOauthNickname().isBlank()) {
            return user.getOauthNickname();
        }
        return "service-user";
    }

    private Long resolveUserId(Users user) {
        return user != null && user.getUserId() != null ? user.getUserId() : 1007L;
    }

    private <T> List<T> paginate(List<T> values, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? values.size() : size;
        int fromIndex = Math.min((safePage - 1) * safeSize, values.size());
        int toIndex = Math.min(fromIndex + safeSize, values.size());
        return values.subList(fromIndex, toIndex);
    }
}
