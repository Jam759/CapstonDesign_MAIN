package com.Hoseo.CapstoneDesign.analysis.listener;

import com.Hoseo.CapstoneDesign.analysis.event.AnalysisJobDispatchRequestedEvent;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisJobDispatchEventListener {

    private final AnalysisJobDispatchService analysisJobDispatchService;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AnalysisJobDispatchRequestedEvent event) {
        try {
            analysisJobDispatchService.dispatchIfPending(event.jobId(), event.traceId());
        } catch (Exception e) {
            log.warn("Failed to dispatch analysis job after commit. jobId={}", event.jobId(), e);
        }
    }
}
