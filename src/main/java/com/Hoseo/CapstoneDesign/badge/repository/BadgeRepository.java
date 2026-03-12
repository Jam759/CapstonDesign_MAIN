package com.Hoseo.CapstoneDesign.badge.repository;

import com.Hoseo.CapstoneDesign.badge.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
