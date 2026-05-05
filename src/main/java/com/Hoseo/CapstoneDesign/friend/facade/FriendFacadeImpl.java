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
    private final UserService userService;

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

        if (friendShipService.areFriends(me, targetUser)) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

        FriendRequest request = friendRequestService.createRequest(me, targetUser);
        return FriendDtoFactory.toInviteResponse(request);
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse acceptInvite(AuthenticatedUserCacheEntry user, Long inviteId) {
        Users me = userService.getReferenceById(user.userId());
        FriendRequest request = friendRequestService.getPendingRequest(inviteId);

        validateReceiverOwnership(request, me.getUserId());

        request.accept();
        friendShipService.createFriendship(request.getRequester(), request.getReceiver());

        return FriendDtoFactory.toInviteResponse(request);
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse declineInvite(AuthenticatedUserCacheEntry user, Long inviteId) {
        Users me = userService.getReferenceById(user.userId());
        FriendRequest request = friendRequestService.getPendingRequest(inviteId);

        validateReceiverOwnership(request, me.getUserId());

        request.reject();

        return FriendDtoFactory.toInviteResponse(request);
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse cancelInviteRequest(AuthenticatedUserCacheEntry authenticatedUser, Long inviteId) {
        Users me = userService.getReferenceById(authenticatedUser.userId());
        FriendRequest request = friendRequestService.getPendingRequest(inviteId);

        validateRequesterOwnership(request, me.getUserId());

        request.cancel();

        return FriendDtoFactory.toInviteResponse(request);
    }

    // 중복 제거를 위한 권한 검증 프라이빗 메서드
    private void validateReceiverOwnership(FriendRequest request, Long userId) {
        if (!request.getReceiver().getUserId().equals(userId)) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND);
        }
    }

    private void validateRequesterOwnership(FriendRequest request, Long userId) {
        if (!request.getRequester().getUserId().equals(userId)) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND);
        }
    }
}