package com.Hoseo.CapstoneDesign.friend.facade;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.friend.factory.FriendDtoFactory;
import com.Hoseo.CapstoneDesign.friend.service.FriendshipService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class FriendFacadeImpl implements FriendFacade {

    private final FriendshipService friendshipService;

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(Users user) {
        return friendshipService.getAcceptedFriends(user.getUserId())
                .stream()
                .map(f -> FriendDtoFactory.toFriendResponse(f, user.getUserId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendInviteResponse> getInvites(Users user) {
        return friendshipService.getPendingInvites(user.getUserId())
                .stream()
                .map(FriendDtoFactory::toInviteResponse)
                .toList();
    }

    @Override
    @Transactional
    public FriendInviteResponse sendRequest(Users user, Long targetUserId) {
        return FriendDtoFactory.toInviteResponse(
                friendshipService.sendRequest(user, targetUserId)
        );
    }

    @Override
    @Transactional
    public FriendInviteResponse acceptInvite(Users user, Long inviteId) {
        return FriendDtoFactory.toInviteResponse(
                friendshipService.acceptInvite(user.getUserId(), inviteId)
        );
    }

    @Override
    @Transactional
    public FriendInviteResponse declineInvite(Users user, Long inviteId) {
        return FriendDtoFactory.toInviteResponse(
                friendshipService.declineInvite(user.getUserId(), inviteId)
        );
    }
}
