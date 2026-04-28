package com.Hoseo.CapstoneDesign.project.cache.listener;

import com.Hoseo.CapstoneDesign.project.cache.service.ProjectResponseCacheService;
import com.Hoseo.CapstoneDesign.project.event.ProjectMembershipChangedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ProjectCacheInvalidationListenerTest {

    private final ProjectResponseCacheService cacheService = mock(ProjectResponseCacheService.class);
    private final ProjectCacheInvalidationListener listener = new ProjectCacheInvalidationListener(cacheService);

    @Test
    @DisplayName("evicts distinct user project caches")
    void evictsDistinctUserProjectCaches() {
        listener.onProjectMembershipChanged(new ProjectMembershipChangedEvent(10L, List.of(1L, 2L, 1L)));

        verify(cacheService).evictMyProjects(1L);
        verify(cacheService).evictMyProjects(2L);
    }

    @Test
    @DisplayName("ignores empty user ids")
    void ignoresEmptyUserIds() {
        listener.onProjectMembershipChanged(new ProjectMembershipChangedEvent(10L, List.of()));

        verify(cacheService, never()).evictMyProjects(1L);
    }
}
