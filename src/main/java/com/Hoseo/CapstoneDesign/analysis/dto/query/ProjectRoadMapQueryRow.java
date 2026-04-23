package com.Hoseo.CapstoneDesign.analysis.dto.query;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.MilestoneStatus;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.PhaseStatus;

public record ProjectRoadMapQueryRow(
        Long projectId,
        Long projectMemberId,
        Long phaseId,
        Integer phaseOrder,
        String phaseName,
        String phaseObjective,
        String phaseOutcome,
        String exitCriteria,
        PhaseStatus phaseStatus,
        Long phaseScopeId,
        Integer scopeOrder,
        String scope,
        Long milestoneId,
        String milestoneName,
        String milestoneIntent,
        String triggerCondition,
        String expectedState,
        String completionRule,
        MilestoneStatus milestoneStatus,
        Long observableEvidenceId,
        Integer evidenceOrder,
        String evidence
) {
}
