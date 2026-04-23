package com.Hoseo.CapstoneDesign.friend.facade;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.List;

public interface FriendFacade {
    List<FriendResponse> getFriends(Users user);
    List<FriendInviteResponse> getInvites(Users user);
    FriendInviteResponse sendRequest(Users user, Long targetUserId);
    FriendInviteResponse acceptInvite(Users user, Long inviteId);
    FriendInviteResponse declineInvite(Users user, Long inviteId);
}
