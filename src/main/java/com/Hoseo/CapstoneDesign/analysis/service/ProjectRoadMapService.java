package com.Hoseo.CapstoneDesign.analysis.service;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectMilestoneDto;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectPhaseDto;
import com.Hoseo.CapstoneDesign.analysis.dto.query.ProjectRoadMapQueryRow;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectRoadMapResponse;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.MilestoneStatus;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.PhaseStatus;
import com.Hoseo.CapstoneDesign.analysis.mapper.AnalysisRoadMapMapper;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectRoadMapService {

    private final AnalysisRoadMapMapper analysisRoadMapMapper;

    @Transactional(readOnly = true)
    public ProjectRoadMapResponse getRoadMap(Long projectId, Long userId) {
        List<ProjectRoadMapQueryRow> rows = analysisRoadMapMapper.findRoadMapRows(projectId, userId);
        validateRoadMapAccess(rows);

        RoadMapAccumulator accumulator = toRoadMapAccumulator(rows);
        List<ProjectPhaseDto> phaseDtos = accumulator.toPhaseDtos();
        List<ProjectMilestoneDto> milestoneDtos = accumulator.toMilestoneDtos();

        return new ProjectRoadMapResponse(
                calculateOverallProgress(phaseDtos, milestoneDtos),
                phaseDtos,
                milestoneDtos
        );
    }

    private void validateRoadMapAccess(List<ProjectRoadMapQueryRow> rows) {
        if (rows.isEmpty()) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_NOT_FOUND);
        }

        if (rows.getFirst().projectMemberId() == null) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        }
    }

    private RoadMapAccumulator toRoadMapAccumulator(List<ProjectRoadMapQueryRow> rows) {
        Map<Long, PhaseAccumulator> phases = new LinkedHashMap<>();
        Map<Long, MilestoneAccumulator> milestones = new LinkedHashMap<>();

        for (ProjectRoadMapQueryRow row : rows) {
            if (row.phaseId() == null) {
                continue;
            }

            PhaseAccumulator accumulator = phases.computeIfAbsent(
                    row.phaseId(),
                    ignored -> new PhaseAccumulator(row)
            );
            accumulator.addScope(row);

            if (row.milestoneId() != null) {
                MilestoneAccumulator milestoneAccumulator = milestones.computeIfAbsent(
                        row.milestoneId(),
                        ignored -> new MilestoneAccumulator(row)
                );
                milestoneAccumulator.addObservableEvidence(row);
            }
        }

        return new RoadMapAccumulator(phases, milestones);
    }

    private int calculateOverallProgress(List<ProjectPhaseDto> phases, List<ProjectMilestoneDto> milestones) {
        if (!milestones.isEmpty()) {
            long achievedCount = milestones.stream()
                    .filter(milestone -> milestone.getStatus() == MilestoneStatus.ACHIEVED)
                    .count();

            return toPercentage(achievedCount, milestones.size());
        }

        if (!phases.isEmpty()) {
            long completedCount = phases.stream()
                    .filter(phase -> phase.getStatus() == PhaseStatus.COMPLETED)
                    .count();

            return toPercentage(completedCount, phases.size());
        }

        return 0;
    }

    private int toPercentage(long completedCount, int totalCount) {
        return (int) Math.round((completedCount * 100.0) / totalCount);
    }

    private record RoadMapAccumulator(
            Map<Long, PhaseAccumulator> phases,
            Map<Long, MilestoneAccumulator> milestones
    ) {

        private List<ProjectPhaseDto> toPhaseDtos() {
            return phases.values().stream()
                    .map(PhaseAccumulator::toDto)
                    .toList();
        }

        private List<ProjectMilestoneDto> toMilestoneDtos() {
            return milestones.values().stream()
                    .map(MilestoneAccumulator::toDto)
                    .toList();
        }
    }

    private record PhaseAccumulator(
            ProjectRoadMapQueryRow row,
            List<String> phaseScopes,
            Set<Long> phaseScopeIds
    ) {

        private PhaseAccumulator(ProjectRoadMapQueryRow row) {
            this(row, new ArrayList<>(), new HashSet<>());
        }

        private void addScope(ProjectRoadMapQueryRow row) {
            if (row.phaseScopeId() != null && phaseScopeIds.add(row.phaseScopeId())) {
                phaseScopes.add(row.scope());
            }
        }

        private ProjectPhaseDto toDto() {
            return ProjectPhaseDto.builder()
                    .id(row.phaseId())
                    .phaseOrder(row.phaseOrder())
                    .phaseName(row.phaseName())
                    .phaseObjective(row.phaseObjective())
                    .phaseOutcome(row.phaseOutcome())
                    .phaseScope(List.copyOf(phaseScopes))
                    .exitCriteria(row.exitCriteria())
                    .status(row.phaseStatus())
                    .build();
        }
    }

    private record MilestoneAccumulator(
            ProjectRoadMapQueryRow row,
            List<String> observableEvidences,
            Set<Long> observableEvidenceIds
    ) {

        private MilestoneAccumulator(ProjectRoadMapQueryRow row) {
            this(row, new ArrayList<>(), new HashSet<>());
        }

        private void addObservableEvidence(ProjectRoadMapQueryRow row) {
            if (row.observableEvidenceId() != null && observableEvidenceIds.add(row.observableEvidenceId())) {
                observableEvidences.add(row.evidence());
            }
        }

        private ProjectMilestoneDto toDto() {
            return ProjectMilestoneDto.builder()
                    .id(row.milestoneId())
                    .phaseId(row.phaseId())
                    .milestoneName(row.milestoneName())
                    .milestoneIntent(row.milestoneIntent())
                    .triggerCondition(row.triggerCondition())
                    .expectedState(row.expectedState())
                    .observableEvidence(List.copyOf(observableEvidences))
                    .completionRule(row.completionRule())
                    .status(row.milestoneStatus())
                    .build();
        }
    }
}
