package com.Hoseo.CapstoneDesign.analysis.cache.listener;

import com.Hoseo.CapstoneDesign.analysis.cache.service.ProjectAnalysisUserViewCacheService;
import com.Hoseo.CapstoneDesign.analysis.event.ProjectAnalysisReportPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AnalysisCacheInvalidationListener {

    private final ProjectAnalysisUserViewCacheService cacheService;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictUserView(ProjectAnalysisReportPublishedEvent event) {
        cacheService.evictUserView(event.projectId(), event.version());
    }
}
