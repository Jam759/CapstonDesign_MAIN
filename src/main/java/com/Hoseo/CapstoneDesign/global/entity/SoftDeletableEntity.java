package com.Hoseo.CapstoneDesign.global.entity;

import com.Hoseo.CapstoneDesign.global.entity.interfaces.CustomSoftDeletable;
import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class SoftDeletableEntity implements CustomSoftDeletable {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Override
    public void softDelete() {
        this.deletedAt = TimeUtil.getNowSeoulLocalDateTime();
    }

    @Override
    public void restore() {
        this.deletedAt = null;
    }
}
