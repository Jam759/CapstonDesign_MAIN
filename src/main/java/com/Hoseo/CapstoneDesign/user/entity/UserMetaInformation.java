package com.Hoseo.CapstoneDesign.user.entity;

import com.Hoseo.CapstoneDesign.exp.entity.LevelRule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
@Table(name = "user_meta_information") // ERD original table: user_meta_infomations
public class UserMetaInformation {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "total_exp", nullable = false)
    private Long totalExp;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level", nullable = false, referencedColumnName = "level")
    private LevelRule levelRule;
}
