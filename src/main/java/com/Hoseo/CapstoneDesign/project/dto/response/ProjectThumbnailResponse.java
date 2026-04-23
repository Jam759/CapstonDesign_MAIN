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
@Schema(description = "Project thumbnail response")
public class ProjectThumbnailResponse {

    @Schema(description = "Project id", example = "101")
    private Long projectId;

    @Schema(description = "Frontend-friendly project id alias", example = "101")
    private Long id;

    @Schema(description = "Project title", example = "DevXP")
    private String title;

    @Schema(description = "Frontend-friendly project name alias", example = "DevXP")
    private String name;

    @Schema(description = "Project type used by frontend cards", example = "team")
    private String type;

    @Schema(description = "Current user's frontend project role", example = "backend")
    private String role;

    @Schema(description = "Project description", example = "GitHub analysis based developer growth service")
    private String description;

    @Schema(description = "Project start datetime", example = "2026-04-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Project end datetime", example = "2026-06-30T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "Tech stack ids")
    private List<String> techStack;

    @Schema(description = "Frontend-friendly tech stack alias", example = "[\"react\", \"spring\"]")
    private List<String> stacks;

    @Schema(description = "Connected repository full name", example = "Jam759/CapstoneDesign")
    private String githubRepo;

    @Schema(description = "Tracked branch name", example = "main")
    private String githubBranch;

    @Schema(description = "Project member display names", example = "[\"alice\", \"bob\"]")
    private List<String> teamMembers;
}
