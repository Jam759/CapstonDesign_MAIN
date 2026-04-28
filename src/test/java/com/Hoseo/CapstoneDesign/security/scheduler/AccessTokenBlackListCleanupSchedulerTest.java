package com.Hoseo.CapstoneDesign.security.scheduler;

import com.Hoseo.CapstoneDesign.security.repository.AccessTokenBlackListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenBlackListCleanupSchedulerTest {

    @Mock
    private AccessTokenBlackListRepository repository;

    private AccessTokenBlackListCleanupScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new AccessTokenBlackListCleanupScheduler(repository);
    }

    @Test
    @DisplayName("deletes expired access token blacklist rows")
    void deleteExpiredBlackList() {
        when(repository.deleteExpired(any(LocalDateTime.class))).thenReturn(1);

        scheduler.deleteExpiredBlackList();

        verify(repository).deleteExpired(any(LocalDateTime.class));
    }
}
