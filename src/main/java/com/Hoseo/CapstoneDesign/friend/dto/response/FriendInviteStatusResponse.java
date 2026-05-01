package com.Hoseo.CapstoneDesign.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Friend or project invite response")
public record FriendInviteStatusResponse(

        //요청
        List<FriendInviteResponse> inviteRequestList,
        //요청응답
        List<FriendInviteResponse> receivedInviteList

) {
}
