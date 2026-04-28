package com.Hoseo.CapstoneDesign.analysis.cache.listener;

import com.Hoseo.CapstoneDesign.analysis.cache.service.ProjectAnalysisUserViewCacheService;
import com.Hoseo.CapstoneDesign.analysis.event.ProjectAnalysisReportPublishedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AnalysisCacheInvalidationListenerTest {

    @Test
    @DisplayName("evicts latest and version USER_VIEW cache when report is published")
    void evictUserView() {
        ProjectAnalysisUserViewCacheService cacheService = mock(ProjectAnalysisUserViewCacheService.class);
        AnalysisCacheInvalidationListener listener = new AnalysisCacheInvalidationListener(cacheService);

        listener.evictUserView(new ProjectAnalysisReportPublishedEvent(1L, 3, 20L));

        verify(cacheService).evictUserView(1L, 3);
    }
}
