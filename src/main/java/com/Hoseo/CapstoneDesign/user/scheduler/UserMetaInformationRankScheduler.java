package com.Hoseo.CapstoneDesign.user.scheduler;

import com.Hoseo.CapstoneDesign.user.service.UserMetaInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMetaInformationRankScheduler {

    private final UserMetaInformationService userMetaInformationService;

    @Scheduled(fixedDelayString = "${app.user-meta.rank.rebuild-delay-ms:5000}")
    public void rebuildRanksIfDirty() {
        if (userMetaInformationService.rebuildRanksIfRequested()) {
            log.info("Rebuilt user meta information ranks.");
        }
    }
}
