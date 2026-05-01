package com.Hoseo.CapstoneDesign.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "받은 요청 및 자신의 요청 목록 dto")
public record FriendInviteStatusResponse(

        //요청
        List<FriendInviteResponse> inviteRequestList,
        //요청응답
        List<FriendInviteResponse> receivedInviteList

) {
}
