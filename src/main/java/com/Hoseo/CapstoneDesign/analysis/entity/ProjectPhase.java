package com.Hoseo.CapstoneDesign.analysis.entity;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.PhaseStatus;
import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_phase")
public class ProjectPhase extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_phase_id", nullable = false)
    private Long projectPhaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    @Column(name = "phase_order", nullable = false)
    private Integer phaseOrder;

    @Column(name = "phase_name", nullable = false, length = 100)
    private String phaseName;

    @Column(name = "phase_objective", columnDefinition = "TEXT")
    private String phaseObjective;

    @Column(name = "phase_outcome", columnDefinition = "TEXT")
    private String phaseOutcome;

    @Column(name = "exit_criteria", columnDefinition = "TEXT")
    private String exitCriteria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PhaseStatus status;
}
