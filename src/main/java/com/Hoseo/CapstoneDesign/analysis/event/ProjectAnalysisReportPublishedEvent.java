package com.Hoseo.CapstoneDesign.analysis.event;

public record ProjectAnalysisReportPublishedEvent(
        Long projectId,
        Integer version,
        Long reportId
) {
}
