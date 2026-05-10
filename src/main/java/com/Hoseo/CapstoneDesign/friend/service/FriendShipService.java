package com.Hoseo.CapstoneDesign.friend.service;

import com.Hoseo.CapstoneDesign.friend.entity.Friendship;
import com.Hoseo.CapstoneDesign.friend.exception.FriendErrorCode;
import com.Hoseo.CapstoneDesign.friend.exception.FriendException;
import com.Hoseo.CapstoneDesign.friend.repository.FriendshipRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendShipService {

    private final FriendshipRepository repository;

    // 두 유저가 이미 친구인지 확인합니다. (ID 대소 비교 규칙 적용)
    @Transactional(readOnly = true)
    public boolean areFriends(Users u1, Users u2) {
        Users user = u1.getUserId() < u2.getUserId() ? u1 : u2;
        Users friendUser = u1.getUserId() < u2.getUserId() ? u2 : u1;
        return repository.existsByUserAndFriendUser(user, friendUser);
    }

    // 새로운 친구 관계를 생성하고 저장합니다.
    @Transactional
    public void createFriendship(Users u1, Users u2) {
        if (areFriends(u1, u2)) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

        Users user = u1.getUserId() < u2.getUserId() ? u1 : u2;
        Users friendUser = u1.getUserId() < u2.getUserId() ? u2 : u1;

        Friendship friendship = Friendship.builder()
                .user(user)
                .friendUser(friendUser)
                .build();

        repository.save(friendship);
    }

    // 내 친구 목록 전체를 가져옵니다.
    @Transactional(readOnly = true)
    public List<Friendship> getFriendships(Users user) {
        return repository.findAllByUserOrFriendUser(user, user);
    }
}