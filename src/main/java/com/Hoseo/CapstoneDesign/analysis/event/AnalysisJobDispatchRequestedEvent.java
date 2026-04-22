package com.Hoseo.CapstoneDesign.analysis.event;

public record AnalysisJobDispatchRequestedEvent(Long jobId, String traceId) {
}
