package com.Hoseo.CapstoneDesign.analysis.factory;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectMilestoneDto;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectPhaseDto;
import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectMilestone;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectMilestoneObservableEvidence;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectPhase;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectPhaseScope;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsBaseMessage;

import java.util.List;

public class AnalysisDtoFactory {

    public static SqsBaseMessage toSqsAnalysisQueueMessage(AnalysisJob savedJob) {
        return SqsBaseMessage.builder()
                .userId(savedJob.getUser().getUserId())
                .jobId(savedJob.getAnalysisJobId().toString())
                .type(savedJob.getAnalysisEventType().name())
                .build();
    }

    public static ProjectPhaseDto toProjectPhaseDto(ProjectPhase phase) {
        return toProjectPhaseDto(phase, List.of());
    }

    public static ProjectPhaseDto toProjectPhaseDto(ProjectPhase phase, List<ProjectPhaseScope> phaseScopes) {
        return ProjectPhaseDto.builder()
                .id(phase.getProjectPhaseId())
                .phaseOrder(phase.getPhaseOrder())
                .phaseName(phase.getPhaseName())
                .phaseObjective(phase.getPhaseObjective())
                .phaseOutcome(phase.getPhaseOutcome())
                .phaseScope(toPhaseScopeValues(phaseScopes))
                .exitCriteria(phase.getExitCriteria())
                .status(phase.getStatus())
                .build();
    }

    public static ProjectMilestoneDto toProjectMilestoneDto(ProjectMilestone milestone) {
        return toProjectMilestoneDto(milestone, List.of());
    }

    public static ProjectMilestoneDto toProjectMilestoneDto(
            ProjectMilestone milestone,
            List<ProjectMilestoneObservableEvidence> observableEvidences
    ) {
        return ProjectMilestoneDto.builder()
                .id(milestone.getProjectMilestoneId())
                .phaseId(milestone.getPhase().getProjectPhaseId())
                .milestoneName(milestone.getMilestoneName())
                .milestoneIntent(milestone.getMilestoneIntent())
                .triggerCondition(milestone.getTriggerCondition())
                .expectedState(milestone.getExpectedState())
                .observableEvidence(toObservableEvidenceValues(observableEvidences))
                .completionRule(milestone.getCompletionRule())
                .status(milestone.getStatus())
                .build();
    }

    private static List<String> toPhaseScopeValues(List<ProjectPhaseScope> phaseScopes) {
        return phaseScopes.stream()
                .map(ProjectPhaseScope::getScope)
                .toList();
    }

    private static List<String> toObservableEvidenceValues(
            List<ProjectMilestoneObservableEvidence> observableEvidences
    ) {
        return observableEvidences.stream()
                .map(ProjectMilestoneObservableEvidence::getEvidence)
                .toList();
    }
}
