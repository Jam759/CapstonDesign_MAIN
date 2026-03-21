package com.Hoseo.CapstoneDesign.gamification.repository;

import com.Hoseo.CapstoneDesign.gamification.entity.UserExpLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExpLogRepository extends JpaRepository<UserExpLog, Long> {
}
