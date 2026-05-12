package com.Hoseo.CapstoneDesign.library.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.library.dto.response.LearningRecommendationResponse;
import com.Hoseo.CapstoneDesign.library.service.LearningRecommendationService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library")
@Tag(name = "Learning Library", description = "Learning recommendation API entry points")
@SecurityRequirement(name = "bearerAuth")
public class LearningLibraryController {

    private final LearningRecommendationService learningRecommendationService;

    @GetMapping("/projects/{projectId}/recommendations")
    @Operation(
            summary = "Get learning recommendations",
            description = "Returns external learning contents based on keywords from the latest completed project analysis."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recommendations returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LearningRecommendationResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<LearningRecommendationResponse>> getRecommendations(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Project id", example = "101")
            @PathVariable Long projectId
    ) {
        List<LearningRecommendationResponse> response = learningRecommendationService.recommend(
                projectId,
                userDetail.getAuthenticatedUser()
        );
        return ResponseEntity.ok(response);
    }
}
