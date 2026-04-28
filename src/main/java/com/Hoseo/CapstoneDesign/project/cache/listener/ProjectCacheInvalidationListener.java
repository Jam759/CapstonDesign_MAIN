package com.Hoseo.CapstoneDesign.project.cache.listener;

import com.Hoseo.CapstoneDesign.project.cache.service.ProjectResponseCacheService;
import com.Hoseo.CapstoneDesign.project.event.ProjectMembershipChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProjectCacheInvalidationListener {

    private final ProjectResponseCacheService projectResponseCacheService;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProjectMembershipChanged(ProjectMembershipChangedEvent event) {
        if (event.userIds() == null || event.userIds().isEmpty()) {
            return;
        }

        event.userIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(projectResponseCacheService::evictMyProjects);
    }
}
