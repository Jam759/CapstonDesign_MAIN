package com.Hoseo.CapstoneDesign.friend.factory;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.friend.entity.Friendship;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public class FriendDtoFactory {

    public static FriendResponse toFriendResponse(Friendship friendship, Long myUserId) {
        Users friend = friendship.getRequester().getUserId().equals(myUserId)
                ? friendship.getReceiver()
                : friendship.getRequester();
        return new FriendResponse(friend.getUserId(), friend.getServiceNickname(), "offline", null);
    }

    public static FriendInviteResponse toInviteResponse(Friendship friendship) {
        return new FriendInviteResponse(
                friendship.getId(),
                friendship.getRequester().getServiceNickname(),
                null,
                friendship.getStatus().name().toLowerCase()
        );
    }
}
