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
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "common_group")
@SQLDelete(sql = "UPDATE common_group SET deleted_at = now() WHERE common_group_id = ?")
public class CommonGroup extends LifecycleTimestampEntity {

    @Id
    @Column(name = "common_group_id", nullable = false, length = 50)
    private String commonGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by", nullable = false)
    private Users deletedBy;
}
