package com.Hoseo.CapstoneDesign.project.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Project create request")
public record ProjectCreateRequest(

        @JsonAlias("projectTitle")
        @Schema(description = "Project name shown in the frontend", example = "DevXP")
        String name,

        @Schema(description = "Project description", example = "GitHub analysis based developer growth service")
        String description,

        @Schema(description = "Project goal", example = "Ship MVP by the end of semester")
        String goal,

        @Schema(description = "Project start date from frontend date input", example = "2026-04-01")
        LocalDate startDate,

        @Schema(description = "Project end date from frontend date input", example = "2026-06-30")
        LocalDate endDate,

        @Schema(description = "Owner role selected in frontend", example = "backend")
        String role,

        @JsonAlias("useTechStackCmdList")
        @Schema(description = "Tech stack common detail ids", example = "[\"react\", \"spring\"]")
        List<String> stacks,

        @JsonAlias("inviteMemberIdList")
        @Schema(description = "Friend user ids to invite as project members", example = "[2, 3]")
        List<Long> teamMemberIds,

        @Schema(
                description = "Repository id from GET /api/v1/github/repositories",
                example = "3001"
        )
        Long installationRepositoryId,

        @Schema(description = "Tracked branch name", example = "main")
        String trackedBranch
) {
}
