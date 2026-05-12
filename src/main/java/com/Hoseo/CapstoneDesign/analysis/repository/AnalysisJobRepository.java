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

    Optional<AnalysisJob> findFirstByProjectProjectIdAndJobStatusOrderByCreatedAtDesc(
            Long projectId,
            AnalysisJobStatus jobStatus
    );

    /**
     * 해당 프로젝트에 대해 지정 상태(예: NOTIFICATION_COMPLETED) 의 분석 Job 이 1건이라도 존재하는지.
     * 대시보드/로드맵/리포트 진입 가드에서 "첫 분석 완료 여부" 체크용.
     */
    boolean existsByProject_ProjectIdAndJobStatus(Long projectId, AnalysisJobStatus jobStatus);
}
