package com.Hoseo.CapstoneDesign.user.cache.listener;

import com.Hoseo.CapstoneDesign.security.cache.service.AuthenticatedUserCacheService;
import com.Hoseo.CapstoneDesign.user.cache.service.UserResponseCacheService;
import com.Hoseo.CapstoneDesign.user.event.UserProfileChangedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class UserCacheInvalidationListenerTest {

    private final UserResponseCacheService cacheService = mock(UserResponseCacheService.class);
    private final AuthenticatedUserCacheService authenticatedUserCacheService = mock(AuthenticatedUserCacheService.class);
    private final UserCacheInvalidationListener listener =
            new UserCacheInvalidationListener(cacheService, authenticatedUserCacheService);

    @Test
    @DisplayName("evicts my info cache when profile changes")
    void evictsMyInfoCacheWhenProfileChanges() {
        UUID identityId = UUID.randomUUID();

        listener.onUserProfileChanged(new UserProfileChangedEvent(1L, identityId));

        verify(cacheService).evictMyInfo(1L);
        verify(authenticatedUserCacheService).evict(identityId);
    }

    @Test
    @DisplayName("ignores null user id")
    void ignoresNullUserId() {
        listener.onUserProfileChanged(new UserProfileChangedEvent(null, null));

        verify(cacheService, never()).evictMyInfo(1L);
        verify(authenticatedUserCacheService, never()).evict(any());
    }
}
