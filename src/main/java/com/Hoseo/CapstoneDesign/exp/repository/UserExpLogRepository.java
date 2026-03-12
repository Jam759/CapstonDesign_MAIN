package com.Hoseo.CapstoneDesign.exp.repository;

import com.Hoseo.CapstoneDesign.exp.entity.UserExpLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExpLogRepository extends JpaRepository<UserExpLog, Long> {
}
