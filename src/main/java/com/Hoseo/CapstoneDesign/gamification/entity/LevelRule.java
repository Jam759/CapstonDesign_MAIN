package com.Hoseo.CapstoneDesign.gamification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "level_rules")
public class LevelRule {

    @Id
    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "required_total_exp")
    private Long requiredTotalExp;
}
