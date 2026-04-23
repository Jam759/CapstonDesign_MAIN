package com.Hoseo.CapstoneDesign.analysis.repository;

import com.Hoseo.CapstoneDesign.analysis.entity.ProjectPhaseScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectPhaseScopeRepository extends JpaRepository<ProjectPhaseScope, Long> {

    List<ProjectPhaseScope> findByPhaseProjectPhaseIdOrderByScopeOrderAscProjectPhaseScopeIdAsc(Long projectPhaseId);

    List<ProjectPhaseScope> findByPhaseProjectProjectIdOrderByPhasePhaseOrderAscScopeOrderAscProjectPhaseScopeIdAsc(
            Long projectId
    );
}
