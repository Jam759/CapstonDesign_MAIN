package com.Hoseo.CapstoneDesign.gamification.controller;

import com.Hoseo.CapstoneDesign.gamification.dto.response.BadgeResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.QuestResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.RankingResponse;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import com.Hoseo.CapstoneDesign.gamification.facade.GamificationFacade;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Gamification", description = "경험치, 퀘스트, 배지 API")
@SecurityRequirement(name = "bearerAuth")
public class GamificationController {

    private final GamificationFacade facade;

    @GetMapping("/xp/ranking")
    @Operation(
            summary = "경험치 랭킹 조회",
            description = "현재 구현은 실제 랭킹 집계 대신 mock 랭킹 목록을 페이지 단위로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "랭킹 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<RankingResponse>> getRanking(
            @Parameter(description = "페이지 번호(1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "8")
            @RequestParam(defaultValue = "8") Integer size
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<RankingResponse> res =
//                facade.getRanking(page, size);
        List<RankingResponse> res = facade.getRanking(page, size);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/xp")
    @Operation(
            summary = "내 경험치 랭킹 조회",
            description = "현재 구현은 실제 집계 대신 mock 내 랭킹 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내 랭킹 조회 성공",
                    content = @Content(schema = @Schema(implementation = RankingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<RankingResponse> getMyRank(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        RankingResponse myRank =
//                facade.getMyRank(userDetail.getUser());
        RankingResponse myRank = facade.getMyRank(userDetail != null ? userDetail.getUser() : null);
        return ResponseEntity.ok(myRank);
    }

    @GetMapping("/quests")
    @Operation(
            summary = "내 퀘스트 목록 조회",
            description = "현재 구현은 상태별로 필터링된 mock 퀘스트 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "퀘스트 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<QuestResponse>> getQuestResponse(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "조회할 퀘스트 진행 상태", example = "ACTIVE")
            @RequestParam AiQuestProgressStatus progressStatus,
            @Parameter(description = "페이지 번호(1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "8")
            @RequestParam(defaultValue = "8") Integer size
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<QuestResponse> res =
//                facade.getMyQuest(userDetail.getUser(), progressStatus, page, size);
        List<QuestResponse> res =
                facade.getMyQuest(userDetail != null ? userDetail.getUser() : null, progressStatus, page, size);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/badges")
    @Operation(
            summary = "내 배지 목록 조회",
            description = "현재 구현은 실제 보유 배지 대신 mock 배지 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "배지 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BadgeResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<BadgeResponse>> getMyBadges(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<BadgeResponse> res = facade.getMyBadges(userDetail.getUser());
        List<BadgeResponse> res = facade.getMyBadges(userDetail != null ? userDetail.getUser() : null);
        return ResponseEntity.ok(res);
    }
}
