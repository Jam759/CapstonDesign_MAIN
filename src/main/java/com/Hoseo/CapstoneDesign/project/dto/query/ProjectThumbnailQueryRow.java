package com.Hoseo.CapstoneDesign.project.dto.query;

import java.time.LocalDateTime;

public record ProjectThumbnailQueryRow(
        Long projectId,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String trackedBranch,
        String repositoryFullName,
        String role,
        String techStacksCsv,
        String teamMembersCsv
) {
}
