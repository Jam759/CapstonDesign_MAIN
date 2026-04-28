package com.Hoseo.CapstoneDesign.friend.facade;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.friend.factory.FriendDtoFactory;
import com.Hoseo.CapstoneDesign.friend.service.FriendshipService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class FriendFacadeImpl implements FriendFacade {

    private final FriendshipService friendshipService;

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(AuthenticatedUserCacheEntry user) {
        return friendshipService.getAcceptedFriends(user.userId())
                .stream()
                .map(f -> FriendDtoFactory.toFriendResponse(f, user.userId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendInviteResponse> getInvites(AuthenticatedUserCacheEntry user) {
        return friendshipService.getPendingInvites(user.userId())
                .stream()
                .map(FriendDtoFactory::toInviteResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse sendRequest(AuthenticatedUserCacheEntry user, Long targetUserId) {
        return FriendDtoFactory.toInviteResponse(
                friendshipService.sendRequest(user.userId(), targetUserId)
        );
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse acceptInvite(AuthenticatedUserCacheEntry user, Long inviteId) {
        return FriendDtoFactory.toInviteResponse(
                friendshipService.acceptInvite(user.userId(), inviteId)
        );
    }

    @Override
    @Transactional(readOnly = false)
    public FriendInviteResponse declineInvite(AuthenticatedUserCacheEntry user, Long inviteId) {
        return FriendDtoFactory.toInviteResponse(
                friendshipService.declineInvite(user.userId(), inviteId)
        );
    }
}
