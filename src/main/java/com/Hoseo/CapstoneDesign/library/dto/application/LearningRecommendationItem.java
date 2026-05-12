package com.Hoseo.CapstoneDesign.library.dto.application;

public record LearningRecommendationItem(
        String type,
        String title,
        String description,
        String source,
        String duration,
        String url,
        String thumbnailUrl
) {
}
