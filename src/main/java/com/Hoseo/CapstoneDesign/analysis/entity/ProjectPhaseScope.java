package com.Hoseo.CapstoneDesign.analysis.entity;

import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "project_phase_scope")
public class ProjectPhaseScope extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_phase_scope_id", nullable = false)
    private Long projectPhaseScopeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_phase_id", nullable = false)
    private ProjectPhase phase;

    @Column(name = "scope_order", nullable = false)
    private Integer scopeOrder;

    @Column(name = "scope", columnDefinition = "TEXT", nullable = false)
    private String scope;
}
