package com.Hoseo.CapstoneDesign.friend.factory;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteStatusResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.friend.entity.FriendRequest;
import com.Hoseo.CapstoneDesign.friend.entity.Friendship;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.List;

public class FriendDtoFactory {

    private FriendDtoFactory() {}

    // Friendship 엔티티에서 상대방(나의 친구) 정보를 추출하여 FriendResponse 로 변환합니다.
    public static FriendResponse toFriendResponse(Users me, Friendship friendship) {
        // 내가 user 컬럼에 있으면 상대방은 friendUser 에 있고, 반대의 경우도 처리합니다.
        Users friend = friendship.getUser().getUserId().equals(me.getUserId())
                ? friendship.getFriendUser()
                : friendship.getUser();

        return new FriendResponse(
                friend.getUserId(),
                friend.getServiceNickname() != null ? friend.getServiceNickname() : "unknown",
                friend.getOauthNickname() != null ? friend.getOauthNickname() : "unknown"
        );
    }
    // FriendRequest 엔티티를 FriendInviteResponse 로 변환합니다.
    public static FriendInviteResponse toInviteResponse(FriendRequest request) {
        return new FriendInviteResponse(
                request.getId(),
                request.getRequester().getServiceNickname() != null ? request.getRequester().getServiceNickname() : "unknown",
                null, // 일반 친구 요청이므로 프로젝트 이름은 null 로 처리합니다.
                request.getStatus().name().toLowerCase()
        );
    }
    // 보낸 요청과 받은 요청 리스트를 묶어서 상태 응답 DTO 로 변환합니다.
    public static FriendInviteStatusResponse toInviteStatusResponse(List<FriendRequest> sentRequests, List<FriendRequest> receivedRequests) {
        List<FriendInviteResponse> sentList = sentRequests.stream()
                .map(FriendDtoFactory::toInviteResponse)
                .toList();

        List<FriendInviteResponse> receivedList = receivedRequests.stream()
                .map(FriendDtoFactory::toInviteResponse)
                .toList();

        return new FriendInviteStatusResponse(sentList, receivedList);
    }
}