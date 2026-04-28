package com.Hoseo.CapstoneDesign.user.cache.listener;

import com.Hoseo.CapstoneDesign.security.cache.service.AuthenticatedUserCacheService;
import com.Hoseo.CapstoneDesign.user.cache.service.UserResponseCacheService;
import com.Hoseo.CapstoneDesign.user.event.UserProfileChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserCacheInvalidationListener {

    private final UserResponseCacheService userResponseCacheService;
    private final AuthenticatedUserCacheService authenticatedUserCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserProfileChanged(UserProfileChangedEvent event) {
        if (event.userId() != null) {
            userResponseCacheService.evictMyInfo(event.userId());
        }
        if (event.identityId() != null) {
            authenticatedUserCacheService.evict(event.identityId());
        }
    }
}
