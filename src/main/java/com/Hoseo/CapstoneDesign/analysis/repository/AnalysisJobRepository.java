package com.Hoseo.CapstoneDesign.analysis.repository;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, Long> {
    Optional<AnalysisJob> findByDeliveryId(String deliveryId);

    boolean existsByDeliveryId(String deliveryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select job from AnalysisJob job where job.analysisJobId = :jobId")
    Optional<AnalysisJob> findByIdForUpdate(@Param("jobId") Long jobId);

    List<AnalysisJob> findByJobStatusOrderByCreatedAtAsc(AnalysisJobStatus jobStatus, Pageable pageable);
}
