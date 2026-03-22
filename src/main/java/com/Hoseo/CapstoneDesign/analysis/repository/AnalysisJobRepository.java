package com.Hoseo.CapstoneDesign.analysis.repository;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, Long> {
    Optional<AnalysisJob> findByDeliveryId(String deliveryId);

    boolean existsByDeliveryId(String deliveryId);
}
