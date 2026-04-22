package com.Hoseo.CapstoneDesign.analysis.repository;

import com.Hoseo.CapstoneDesign.analysis.entity.ProjectMilestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMilestoneRepository extends JpaRepository<ProjectMilestone, Long> {

    List<ProjectMilestone> findByProjectProjectIdOrderByPhasePhaseOrderAscProjectMilestoneIdAsc(Long projectId);

    List<ProjectMilestone> findByPhaseProjectPhaseIdOrderByProjectMilestoneIdAsc(Long projectPhaseId);

    Optional<ProjectMilestone> findByProjectMilestoneIdAndProjectProjectId(Long projectMilestoneId, Long projectId);
}
