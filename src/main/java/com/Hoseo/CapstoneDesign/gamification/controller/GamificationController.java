package com.Hoseo.CapstoneDesign.gamification.controller;

import com.Hoseo.CapstoneDesign.gamification.dto.response.BadgeResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.QuestResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.RankingResponse;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import com.Hoseo.CapstoneDesign.gamification.facade.GamificationFacade;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gamification")
public class GamificationController {

    private final GamificationFacade facade;

    @GetMapping("/xp/ranking")
    public ResponseEntity<List<RankingResponse>> getRanking(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer size
    ){
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<RankingResponse> res =
//                facade.getRanking(page, size);
        List<RankingResponse> res = facade.getRanking(page, size);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/xp")
    public ResponseEntity<RankingResponse> getMyRank(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ){
        // TODO : 추후 구현 현재는 mock데이터 반환
//        RankingResponse myRank =
//                facade.getMyRank(userDetail.getUser());
        RankingResponse myRank = facade.getMyRank(userDetail != null ? userDetail.getUser() : null);
        return ResponseEntity.ok(myRank);
    }

    @GetMapping("/quests")
    public ResponseEntity<List<QuestResponse>> getQuestResponse(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam AiQuestProgressStatus progressStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer size
    ){
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<QuestResponse> res =
//                facade.getMyQuest(userDetail.getUser(), progressStatus, page, size);
        List<QuestResponse> res =
                facade.getMyQuest(userDetail != null ? userDetail.getUser() : null, progressStatus, page, size);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/badges")
    public ResponseEntity<List<BadgeResponse>> getMyBadges(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<BadgeResponse> res = facade.getMyBadges(userDetail.getUser());
        List<BadgeResponse> res = facade.getMyBadges(userDetail != null ? userDetail.getUser() : null);
        return ResponseEntity.ok(res);
    }

}
