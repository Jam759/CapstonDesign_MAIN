package com.Hoseo.CapstoneDesign.friend.facade;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteStatusResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.friend.service.FriendRequestService;
import com.Hoseo.CapstoneDesign.friend.service.FriendShipService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class FriendFacadeImpl implements FriendFacade{

    private final FriendRequestService friendRequestService;
    private final FriendShipService friendShipService;

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(AuthenticatedUserCacheEntry user) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public FriendInviteStatusResponse getInvites(AuthenticatedUserCacheEntry user) {
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse sendRequest(AuthenticatedUserCacheEntry user, Long targetUserId) {
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse acceptInvite(AuthenticatedUserCacheEntry user, Long inviteId) {
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse declineInvite(AuthenticatedUserCacheEntry user, Long inviteId) {
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse cancelInviteRequest(AuthenticatedUserCacheEntry authenticatedUser, Long inviteId) {
        return null;
    }

}
