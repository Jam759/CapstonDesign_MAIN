package com.Hoseo.CapstoneDesign.friend.repository;

import com.Hoseo.CapstoneDesign.friend.entity.FriendRequest;
import com.Hoseo.CapstoneDesign.friend.entity.enums.FriendRequestStatus;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    // 내가 보낸 요청 중 특정 상태(예: PENDING)인 것만 조회합니다.
    List<FriendRequest> findAllByRequesterAndStatus(Users requester, FriendRequestStatus status);

    // 내가 받은 요청 중 특정 상태인 것만 조회합니다.
    List<FriendRequest> findAllByReceiverAndStatus(Users receiver, FriendRequestStatus status);

    // 두 사람 사이에 특정 상태의 요청이 이미 존재하는지 확인합니다.
    boolean existsByRequesterAndReceiverAndStatus(Users requester, Users receiver, FriendRequestStatus status);
}