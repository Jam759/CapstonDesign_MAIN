package com.Hoseo.CapstoneDesign.common.entity;

import com.Hoseo.CapstoneDesign.global.entity.LifecycleTimestampEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "common_group_detail")
public class CommonGroupDetail extends LifecycleTimestampEntity {

    @Id
    @Column(name = "common_group_detail_id", nullable = false, length = 50) // ERD original column: common_groupdetail_id
    private String commonGroupDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "common_group_id", nullable = false)
    private CommonGroup commonGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by", nullable = false)
    private Users deletedBy;
}
