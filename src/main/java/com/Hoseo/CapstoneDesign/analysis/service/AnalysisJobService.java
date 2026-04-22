package com.Hoseo.CapstoneDesign.analysis.service;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.analysis.event.AnalysisJobDispatchRequestedEvent;
import com.Hoseo.CapstoneDesign.analysis.exception.AnalysisJobErrorCode;
import com.Hoseo.CapstoneDesign.analysis.exception.AnalysisJobException;
import com.Hoseo.CapstoneDesign.analysis.repository.AnalysisJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;

@Service
@RequiredArgsConstructor
public class AnalysisJobService {
    private final AnalysisJobRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AnalysisJob create(AnalysisJob job) {
        return repository.save(job);
    }

    public AnalysisJob createPendingJob(AnalysisJob job) {
        AnalysisJob savedJob = repository.save(job);
        requestDispatch(savedJob.getAnalysisJobId());
        return savedJob;
    }

    public boolean existsByDeliveryId(String deliveryId) {
        return repository.existsByDeliveryId(deliveryId);
    }

    public AnalysisJob getById(Long jobId) {
        return repository.findById(jobId)
                .orElseThrow(() -> new AnalysisJobException(AnalysisJobErrorCode.ANALYSIS_JOB_NOT_FOUND));
    }

    public AnalysisJob getByIdForUpdate(Long jobId) {
        return repository.findByIdForUpdate(jobId)
                .orElseThrow(() -> new AnalysisJobException(AnalysisJobErrorCode.ANALYSIS_JOB_NOT_FOUND));
    }

    public List<Long> findPendingJobIds(int size) {
        return repository.findByJobStatusOrderByCreatedAtAsc(
                        AnalysisJobStatus.PENDING,
                        PageRequest.of(0, size)
                ).stream()
                .map(AnalysisJob::getAnalysisJobId)
                .toList();
    }

    public void requestDispatch(Long jobId) {
        applicationEventPublisher.publishEvent(
                new AnalysisJobDispatchRequestedEvent(jobId, MDC.get(TRACE_ID))
        );
    }
}
