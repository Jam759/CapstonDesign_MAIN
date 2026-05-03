package com.Hoseo.CapstoneDesign.friend.facade;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteStatusResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.friend.entity.FriendRequest;
import com.Hoseo.CapstoneDesign.friend.entity.Friendship;
import com.Hoseo.CapstoneDesign.friend.exception.FriendErrorCode;
import com.Hoseo.CapstoneDesign.friend.exception.FriendException;
import com.Hoseo.CapstoneDesign.friend.factory.FriendDtoFactory;
import com.Hoseo.CapstoneDesign.friend.service.FriendRequestService;
import com.Hoseo.CapstoneDesign.friend.service.FriendShipService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class FriendFacadeImpl implements FriendFacade {

    private final FriendRequestService friendRequestService;
    private final FriendShipService friendShipService;
    private final UserService userService; // 유저 정보 연동을 위해 추가

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(AuthenticatedUserCacheEntry user) {
        Users me = userService.getReferenceById(user.userId());
        List<Friendship> friendships = friendShipService.getFriendships(me);

        return friendships.stream()
                .map(friendship -> FriendDtoFactory.toFriendResponse(me, friendship))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FriendInviteStatusResponse getInvites(AuthenticatedUserCacheEntry user) {
        Users me = userService.getReferenceById(user.userId());

        List<FriendRequest> sentRequests = friendRequestService.getSentRequests(me);
        List<FriendRequest> receivedRequests = friendRequestService.getReceivedRequests(me);

        return FriendDtoFactory.toInviteStatusResponse(sentRequests, receivedRequests);
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse sendRequest(AuthenticatedUserCacheEntry user, Long targetUserId) {
        Users me = userService.getReferenceById(user.userId());
        Users targetUser = userService.getReferenceById(targetUserId);

        // 이미 친구인지 확인합니다.
        if (friendShipService.areFriends(me, targetUser)) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

        // 친구 요청을 생성하고 저장합니다.
        FriendRequest request = friendRequestService.createRequest(me, targetUser);
        return FriendDtoFactory.toInviteResponse(request);
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse acceptInvite(AuthenticatedUserCacheEntry user, Long inviteId) {
        Users me = userService.getReferenceById(user.userId());
        FriendRequest request = friendRequestService.getPendingRequest(inviteId);

        // 받은 사람 본인만 수락할 수 있습니다.
        if (!request.getReceiver().getUserId().equals(me.getUserId())) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND);
        }

        // 엔티티 상태를 수락으로 변경합니다.
        request.accept();

        // 실제 친구 관계로 등록합니다.
        friendShipService.createFriendship(request.getRequester(), request.getReceiver());

        return FriendDtoFactory.toInviteResponse(request);
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse declineInvite(AuthenticatedUserCacheEntry user, Long inviteId) {
        Users me = userService.getReferenceById(user.userId());
        FriendRequest request = friendRequestService.getPendingRequest(inviteId);

        // 받은 사람 본인만 거절할 수 있습니다.
        if (!request.getReceiver().getUserId().equals(me.getUserId())) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND);
        }

        // 엔티티 상태를 거절로 변경합니다.
        request.reject();

        return FriendDtoFactory.toInviteResponse(request);
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse cancelInviteRequest(AuthenticatedUserCacheEntry authenticatedUser, Long inviteId) {
        Users me = userService.getReferenceById(authenticatedUser.userId());
        FriendRequest request = friendRequestService.getPendingRequest(inviteId);

        // 보낸 사람 본인만 취소할 수 있습니다.
        if (!request.getRequester().getUserId().equals(me.getUserId())) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND);
        }

        // 엔티티 상태를 취소로 변경합니다.
        request.cancel();

        return FriendDtoFactory.toInviteResponse(request);
    }
}