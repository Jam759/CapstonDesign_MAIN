package com.Hoseo.CapstoneDesign.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Project create response")
public class ProjectCreateResponse {
    @Schema(description = "Created project id", example = "101")
    private Long projectId;

    @Schema(description = "Project title", example = "DevXP")
    private String title;

    @Schema(description = "Project description", example = "GitHub analysis based developer growth service")
    private String description;

    @Schema(description = "Connected repository id", example = "3001")
    private Long installationRepositoryId;

    @Schema(description = "Connected repository full name", example = "Jam759/CapstoneDesign")
    private String repositoryFullName;

    @Schema(description = "Tracked branch name", example = "main")
    private String trackedBranch;

    @Schema(description = "Invited member user ids", example = "[2, 3]")
    private List<Long> invitedMemberIds;

    @Schema(description = "Project created datetime", example = "2026-03-30T10:15:00")
    private LocalDateTime createdAt;

}
