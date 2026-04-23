package com.Hoseo.CapstoneDesign.analysis.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PhaseStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    FAILED;

    @JsonValue
    public String toJson() {
        return switch (this) {
            case NOT_STARTED, FAILED -> "not_started";
            case IN_PROGRESS -> "in_progress";
            case COMPLETED -> "completed";
        };
    }
}
