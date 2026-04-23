package com.Hoseo.CapstoneDesign.gamification.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gamification")
@Tag(name = "Gamification", description = "Experience and AI quest APIs")
@SecurityRequirement(name = "bearerAuth")
public class GamificationController {

    private final GamificationFacade facade;

    @GetMapping("/xp/ranking")
    @Operation(
            summary = "Get XP ranking",
            description = "Returns paginated XP rankings."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ranking returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<RankingResponse>> getRanking(
            @Parameter(description = "Page number starting from 1", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size", example = "8")
            @RequestParam(defaultValue = "8") Integer size
    ) {
        List<RankingResponse> res = facade.getRanking(page, size);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/xp")
    @Operation(
            summary = "Get my XP",
            description = "Returns the current user's XP summary."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "XP summary returned",
                    content = @Content(schema = @Schema(implementation = RankingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<RankingResponse> getMyRank(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        RankingResponse myRank = facade.getMyRank(userDetail != null ? userDetail.getUser() : null);
        return ResponseEntity.ok(myRank);
    }

    @GetMapping("/quests")
    @Operation(
            summary = "Get my AI quests",
            description = "Returns AI quests using the frontend status model. Legacy progressStatus filtering is still supported."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quest list returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<QuestResponse>> getQuestResponse(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Project id to filter quests", example = "1")
            @RequestParam Long projectId,
            @Parameter(description = "Legacy progress filter", example = "ACTIVE")
            @RequestParam(required = false) AiQuestProgressStatus progressStatus,
            @Parameter(description = "Frontend status filter: pending, accepted, declined, completed", example = "pending")
            @RequestParam(required = false) String status,
            @Parameter(description = "Page number starting from 1", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size", example = "8")
            @RequestParam(defaultValue = "8") Integer size
    ) {
        List<QuestResponse> res = facade.getMyQuest(
                userDetail != null ? userDetail.getUser() : null,
                projectId,
                progressStatus,
                status,
                page,
                size
        );
        return ResponseEntity.ok(res);
    }

    @PostMapping("/quests/{questId}/accept")
    @Operation(
            summary = "Accept AI quest",
            description = "Accepts an AI quest and returns the updated frontend quest response."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quest accepted",
                    content = @Content(schema = @Schema(implementation = QuestResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<QuestResponse> acceptQuest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Quest id", example = "1")
            @PathVariable Long questId
    ) {
        QuestResponse res = facade.acceptQuest(userDetail != null ? userDetail.getUser() : null, questId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/quests/{questId}/decline")
    @Operation(
            summary = "Decline AI quest",
            description = "Declines an AI quest and returns the updated frontend quest response."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quest declined",
                    content = @Content(schema = @Schema(implementation = QuestResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<QuestResponse> declineQuest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Quest id", example = "1")
            @PathVariable Long questId
    ) {
        QuestResponse res = facade.declineQuest(userDetail != null ? userDetail.getUser() : null, questId);
        return ResponseEntity.ok(res);
    }
}
