package com.Hoseo.CapstoneDesign.gamification.repository;

import com.Hoseo.CapstoneDesign.gamification.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
