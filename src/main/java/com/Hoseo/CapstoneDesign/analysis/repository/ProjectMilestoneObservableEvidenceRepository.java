package com.Hoseo.CapstoneDesign.analysis.repository;

import com.Hoseo.CapstoneDesign.analysis.entity.ProjectMilestoneObservableEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMilestoneObservableEvidenceRepository
        extends JpaRepository<ProjectMilestoneObservableEvidence, Long> {

    List<ProjectMilestoneObservableEvidence> findByMilestoneProjectMilestoneIdOrderByEvidenceOrderAscProjectMilestoneObservableEvidenceIdAsc(
            Long projectMilestoneId
    );

    List<ProjectMilestoneObservableEvidence> findByMilestoneProjectProjectIdOrderByMilestonePhasePhaseOrderAscMilestoneProjectMilestoneIdAscEvidenceOrderAscProjectMilestoneObservableEvidenceIdAsc(
            Long projectId
    );
}
