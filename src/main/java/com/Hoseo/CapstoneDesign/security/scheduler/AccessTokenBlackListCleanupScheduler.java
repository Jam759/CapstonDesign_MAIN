package com.Hoseo.CapstoneDesign.security.scheduler;

import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.security.repository.AccessTokenBlackListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessTokenBlackListCleanupScheduler {

    private final AccessTokenBlackListRepository repository;

    @Transactional
    @Scheduled(
            initialDelayString = "${auth.access-token-blacklist.cleanup.initial-delay-ms:3600000}",
            fixedDelayString = "${auth.access-token-blacklist.cleanup.fixed-delay-ms:3600000}"
    )
    public void deleteExpiredBlackList() {
        int deletedCount = repository.deleteExpired(TimeUtil.getNowSeoulLocalDateTime());
        if (deletedCount > 0) {
            log.info("Deleted expired access token blacklist rows. count={}", deletedCount);
        }
    }
}
