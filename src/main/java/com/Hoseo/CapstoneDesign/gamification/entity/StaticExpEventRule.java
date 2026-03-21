package com.Hoseo.CapstoneDesign.gamification.entity;

import com.Hoseo.CapstoneDesign.gamification.entity.enums.ExpActionType;
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
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "static_exp_event_rule")
@SQLDelete(sql = "UPDATE static_exp_event_rule SET deleted_at = now() WHERE exp_event_rule_id = ?")
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
