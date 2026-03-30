package com.Hoseo.CapstoneDesign.analysis.controller;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisAdviceResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisScorecardResponse;
import com.Hoseo.CapstoneDesign.analysis.facade.AnalysisFacade;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
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
public class AnalysisController {

    private final AnalysisFacade facade;

    @GetMapping("/{projectId}/overview")
    public ProjectAnalysisOverviewResponse getOverview(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam(required = false) Integer version
    ) {
        return facade.getOverview(userDetail.getUser(), projectId, version);
    }

    @GetMapping("/{projectId}/highlights")
    public ProjectAnalysisHighlightsResponse getHighlights(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam(required = false) Integer version
    ) {
        return facade.getHighlights(userDetail.getUser(), projectId, version);
    }

    @GetMapping("/{projectId}/advice")
    public ProjectAnalysisAdviceResponse getAdvice(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam(required = false) Integer version
    ) {
        return facade.getAdvice(userDetail.getUser(), projectId, version);
    }

    @GetMapping("/{projectId}/scorecard")
    public ProjectAnalysisScorecardResponse getScorecard(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam(required = false) Integer version
    ) {
        return facade.getScorecard(userDetail.getUser(), projectId, version);
    }
}
