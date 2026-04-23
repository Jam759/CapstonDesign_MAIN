package com.Hoseo.CapstoneDesign.friend.service;

import com.Hoseo.CapstoneDesign.friend.entity.Friendship;
import com.Hoseo.CapstoneDesign.friend.entity.enums.FriendshipStatus;
import com.Hoseo.CapstoneDesign.friend.exception.FriendErrorCode;
import com.Hoseo.CapstoneDesign.friend.exception.FriendException;
import com.Hoseo.CapstoneDesign.friend.repository.FriendshipRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<Friendship> getAcceptedFriends(Long userId) {
        return friendshipRepository.findAllAcceptedByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Friendship> getPendingInvites(Long userId) {
        return friendshipRepository.findPendingInvitesByReceiverId(userId);
    }

    @Transactional
    public Friendship sendRequest(Users requester, Long receiverId) {
        usersRepository.findById(receiverId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.USER_NOT_FOUND));

        boolean alreadyExists =
                friendshipRepository.existsByRequesterUserIdAndReceiverUserId(requester.getUserId(), receiverId)
                || friendshipRepository.existsByRequesterUserIdAndReceiverUserId(receiverId, requester.getUserId());
        if (alreadyExists) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

        Users managedRequester = usersRepository.getReferenceById(requester.getUserId());
        Users managedReceiver = usersRepository.getReferenceById(receiverId);
        return friendshipRepository.save(Friendship.builder()
                .requester(managedRequester)
                .receiver(managedReceiver)
                .status(FriendshipStatus.PENDING)
                .build());
    }

    @Transactional
    public Friendship acceptInvite(Long userId, Long inviteId) {
        Friendship friendship = friendshipRepository.findByIdAndReceiverUserId(inviteId, userId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND));
        friendship.accept();
        return friendship;
    }

    @Transactional
    public Friendship declineInvite(Long userId, Long inviteId) {
        Friendship friendship = friendshipRepository.findByIdAndReceiverUserId(inviteId, userId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND));
        friendship.decline();
        return friendship;
    }
}
