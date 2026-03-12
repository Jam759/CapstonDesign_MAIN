package com.Hoseo.CapstoneDesign.badge.repository;

import com.Hoseo.CapstoneDesign.badge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
}
