package com.Hoseo.CapstoneDesign.analysis.repository;

import com.Hoseo.CapstoneDesign.analysis.entity.ProjectPhase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectPhaseRepository extends JpaRepository<ProjectPhase, Long> {

    List<ProjectPhase> findByProjectProjectIdOrderByPhaseOrderAscProjectPhaseIdAsc(Long projectId);

    Optional<ProjectPhase> findByProjectPhaseIdAndProjectProjectId(Long projectPhaseId, Long projectId);

    boolean existsByProjectProjectId(Long projectId);
}
