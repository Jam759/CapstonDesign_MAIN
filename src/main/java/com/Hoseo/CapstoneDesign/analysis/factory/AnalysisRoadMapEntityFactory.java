package com.Hoseo.CapstoneDesign.analysis.factory;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectMilestoneDto;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectPhaseDto;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectMilestone;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectMilestoneObservableEvidence;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectPhase;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectPhaseScope;
import com.Hoseo.CapstoneDesign.project.entity.Projects;

import java.util.List;
import java.util.stream.IntStream;

public class AnalysisRoadMapEntityFactory {

    public static ProjectPhase toProjectPhase(Projects project, ProjectPhaseDto dto) {
        return ProjectPhase.builder()
                .project(project)
                .phaseOrder(dto.getPhaseOrder())
                .phaseName(dto.getPhaseName())
                .phaseObjective(dto.getPhaseObjective())
                .phaseOutcome(dto.getPhaseOutcome())
                .exitCriteria(dto.getExitCriteria())
                .status(dto.getStatus())
                .build();
    }

    public static ProjectMilestone toProjectMilestone(
            Projects project,
            ProjectPhase phase,
            ProjectMilestoneDto dto
    ) {
        return ProjectMilestone.builder()
                .project(project)
                .phase(phase)
                .milestoneName(dto.getMilestoneName())
                .milestoneIntent(dto.getMilestoneIntent())
                .triggerCondition(dto.getTriggerCondition())
                .expectedState(dto.getExpectedState())
                .completionRule(dto.getCompletionRule())
                .status(dto.getStatus())
                .build();
    }

    public static List<ProjectPhaseScope> toProjectPhaseScopes(ProjectPhase phase, List<String> phaseScopes) {
        if (phaseScopes == null) {
            return List.of();
        }

        return IntStream.range(0, phaseScopes.size())
                .mapToObj(index -> ProjectPhaseScope.builder()
                        .phase(phase)
                        .scopeOrder(index)
                        .scope(phaseScopes.get(index))
                        .build())
                .toList();
    }

    public static List<ProjectMilestoneObservableEvidence> toProjectMilestoneObservableEvidences(
            ProjectMilestone milestone,
            List<String> observableEvidences
    ) {
        if (observableEvidences == null) {
            return List.of();
        }

        return IntStream.range(0, observableEvidences.size())
                .mapToObj(index -> ProjectMilestoneObservableEvidence.builder()
                        .milestone(milestone)
                        .evidenceOrder(index)
                        .evidence(observableEvidences.get(index))
                        .build())
                .toList();
    }
}
