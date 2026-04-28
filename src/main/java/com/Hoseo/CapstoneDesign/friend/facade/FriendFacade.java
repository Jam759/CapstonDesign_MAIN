package com.Hoseo.CapstoneDesign.friend.facade;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;

import java.util.List;

public interface FriendFacade {
    List<FriendResponse> getFriends(AuthenticatedUserCacheEntry user);
    List<FriendInviteResponse> getInvites(AuthenticatedUserCacheEntry user);
    FriendInviteResponse sendRequest(AuthenticatedUserCacheEntry user, Long targetUserId);
    FriendInviteResponse acceptInvite(AuthenticatedUserCacheEntry user, Long inviteId);
    FriendInviteResponse declineInvite(AuthenticatedUserCacheEntry user, Long inviteId);
}
