package com.Hoseo.CapstoneDesign.exp.entity;

import com.Hoseo.CapstoneDesign.exp.entity.enums.ExpActionType;
import com.Hoseo.CapstoneDesign.global.entity.LifecycleTimestampEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "static_exp_event_rule")
public class StaticExpEventRule extends LifecycleTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exp_event_rule_id", nullable = false)
    private Long expEventRuleId;

    @Column(name = "exp", nullable = false)
    private Short exp;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ExpActionType actionType;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
