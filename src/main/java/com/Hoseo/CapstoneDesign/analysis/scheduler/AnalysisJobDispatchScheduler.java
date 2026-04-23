package com.Hoseo.CapstoneDesign.analysis.scheduler;

import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobDispatchService;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisJobDispatchScheduler {

    private static final int MAX_PENDING_BATCH_SIZE = 20;

    private final AnalysisJobService analysisJobService;
    private final AnalysisJobDispatchService analysisJobDispatchService;

    @Scheduled(fixedDelayString = "${app.analysis.dispatch.retry-delay-ms:15000}")
    public void dispatchPendingJobs() {
        for (Long pendingJobId : analysisJobService.findPendingJobIds(MAX_PENDING_BATCH_SIZE)) {
            try {
                analysisJobDispatchService.dispatchIfPending(pendingJobId, null);
            } catch (Exception e) {
                log.warn("Failed to dispatch pending analysis job from scheduler. jobId={}", pendingJobId, e);
            }
        }
    }
}
