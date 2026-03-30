package com.Hoseo.CapstoneDesign.analysis.dto.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectAnalysisUserViewResponse(
        String schemaVersion,
        String generatedAt,
        Scope scope,
        String headline,
        String summary,
        List<String> strengths,
        List<String> risks,
        List<Advice> advice,
        Scorecard scorecard
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Scope(
            Long projectId,
            Long userId,
            String repositoryFullName,
            String branchName,
            String beforeCommitHash,
            String afterCommitHash
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Advice(
            String id,
            String priority,
            String category,
            String title,
            String body,
            String recommendedAction,
            String expectedImpact
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Scorecard(
            Overall overall,
            List<Category> categories
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Overall(
            Integer score,
            String grade,
            String confidence
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(
            String key,
            String label,
            Integer score,
            String reason,
            List<String> evidence
    ) {
    }
}
