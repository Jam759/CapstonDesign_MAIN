package com.Hoseo.CapstoneDesign.analysis.entity;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.MilestoneStatus;
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
@Table(name = "project_milestone")
public class ProjectMilestone extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_milestone_id", nullable = false)
    private Long projectMilestoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id", nullable = false)
    private ProjectPhase phase;

    @Column(name = "milestone_name", nullable = false, length = 100)
    private String milestoneName;

    @Column(name = "milestone_intent", columnDefinition = "TEXT")
    private String milestoneIntent;

    @Column(name = "trigger_condition", columnDefinition = "TEXT")
    private String triggerCondition;

    @Column(name = "expected_state", columnDefinition = "TEXT")
    private String expectedState;

    @Column(name = "completion_rule", columnDefinition = "TEXT")
    private String completionRule;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MilestoneStatus status;
}
