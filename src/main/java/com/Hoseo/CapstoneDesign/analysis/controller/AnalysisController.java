package com.Hoseo.CapstoneDesign.analysis.controller;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisAdviceResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisScorecardResponse;
import com.Hoseo.CapstoneDesign.analysis.facade.AnalysisFacade;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/analysis")
@Tag(name = "Analysis", description = "프로젝트 분석 결과 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class AnalysisController {

    private final AnalysisFacade facade;

    @GetMapping("/{projectId}/overview")
    @Operation(
            summary = "프로젝트 분석 개요 조회",
            description = "프로젝트 멤버만 조회할 수 있으며, version을 생략하면 최신 USER_VIEW 리포트를 기준으로 개요를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "분석 개요 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProjectAnalysisOverviewResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "프로젝트 멤버가 아님",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 또는 분석 리포트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ProjectAnalysisOverviewResponse getOverview(
            @Parameter(description = "프로젝트 ID", example = "101")
            @PathVariable Long projectId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "조회할 분석 리포트 버전. 생략하면 최신 버전을 반환합니다.", example = "2")
            @RequestParam(required = false) Integer version
    ) {
        return facade.getOverview(userDetail.getUser(), projectId, version);
    }

    @GetMapping("/{projectId}/highlights")
    @Operation(
            summary = "프로젝트 분석 하이라이트 조회",
            description = "강점과 리스크를 묶어 반환합니다. version을 생략하면 최신 USER_VIEW 리포트를 사용합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "분석 하이라이트 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProjectAnalysisHighlightsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "프로젝트 멤버가 아님",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 또는 분석 리포트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ProjectAnalysisHighlightsResponse getHighlights(
            @Parameter(description = "프로젝트 ID", example = "101")
            @PathVariable Long projectId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "조회할 분석 리포트 버전. 생략하면 최신 버전을 반환합니다.", example = "2")
            @RequestParam(required = false) Integer version
    ) {
        return facade.getHighlights(userDetail.getUser(), projectId, version);
    }

    @GetMapping("/{projectId}/advice")
    @Operation(
            summary = "프로젝트 분석 개선 조언 조회",
            description = "우선순위별 개선 조언 목록을 반환합니다. version을 생략하면 최신 USER_VIEW 리포트를 사용합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "분석 조언 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProjectAnalysisAdviceResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "프로젝트 멤버가 아님",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 또는 분석 리포트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ProjectAnalysisAdviceResponse getAdvice(
            @Parameter(description = "프로젝트 ID", example = "101")
            @PathVariable Long projectId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "조회할 분석 리포트 버전. 생략하면 최신 버전을 반환합니다.", example = "2")
            @RequestParam(required = false) Integer version
    ) {
        return facade.getAdvice(userDetail.getUser(), projectId, version);
    }

    @GetMapping("/{projectId}/scorecard")
    @Operation(
            summary = "프로젝트 분석 점수표 조회",
            description = "전체 점수와 카테고리별 세부 점수를 반환합니다. version을 생략하면 최신 USER_VIEW 리포트를 사용합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "분석 점수표 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProjectAnalysisScorecardResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "프로젝트 멤버가 아님",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 또는 분석 리포트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ProjectAnalysisScorecardResponse getScorecard(
            @Parameter(description = "프로젝트 ID", example = "101")
            @PathVariable Long projectId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "조회할 분석 리포트 버전. 생략하면 최신 버전을 반환합니다.", example = "2")
            @RequestParam(required = false) Integer version
    ) {
        return facade.getScorecard(userDetail.getUser(), projectId, version);
    }
}
