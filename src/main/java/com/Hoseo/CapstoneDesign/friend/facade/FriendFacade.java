package com.Hoseo.CapstoneDesign.friend.facade;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteStatusResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;

import java.util.List;

public interface FriendFacade {
    //친구목록 가져온다
    List<FriendResponse> getFriends(AuthenticatedUserCacheEntry user);

    //자신이 보낸 초대 요청 혹은 받은 요청 목록은 가져온다
    FriendInviteStatusResponse getInvites(AuthenticatedUserCacheEntry user);

    //
    FriendInviteResponse sendRequest(AuthenticatedUserCacheEntry user, Long targetUserId);
    FriendInviteResponse acceptInvite(AuthenticatedUserCacheEntry user, Long inviteId);
    FriendInviteResponse declineInvite(AuthenticatedUserCacheEntry user, Long inviteId);
    FriendInviteResponse cancelInviteRequest(AuthenticatedUserCacheEntry authenticatedUser, Long inviteId);
}
