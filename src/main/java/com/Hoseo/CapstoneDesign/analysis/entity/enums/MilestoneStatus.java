package com.Hoseo.CapstoneDesign.analysis.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MilestoneStatus {
    PENDING,
    IN_PROGRESS,
    ACHIEVED,
    FAILED;

    @JsonValue
    public String toJson() {
        return switch (this) {
            case PENDING, FAILED -> "NOT_STARTED";
            case IN_PROGRESS -> "IN_PROGRESS";
            case ACHIEVED -> "ACHIEVED";
        };
    }
}
