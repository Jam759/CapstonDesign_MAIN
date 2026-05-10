package com.Hoseo.CapstoneDesign.friend.service;

import com.Hoseo.CapstoneDesign.friend.entity.FriendRequest;
import com.Hoseo.CapstoneDesign.friend.entity.enums.FriendRequestStatus;
import com.Hoseo.CapstoneDesign.friend.exception.FriendErrorCode;
import com.Hoseo.CapstoneDesign.friend.exception.FriendException;
import com.Hoseo.CapstoneDesign.friend.repository.FriendRequestRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository repository;

    // 양방향으로 대기 중인 요청이 있는지 확인합니다.
    @Transactional(readOnly = true)
    public boolean hasPendingRequest(Users u1, Users u2) {
        boolean sentByU1 = repository.existsByRequesterAndReceiverAndStatus(u1, u2, FriendRequestStatus.PENDING);
        boolean sentByU2 = repository.existsByRequesterAndReceiverAndStatus(u2, u1, FriendRequestStatus.PENDING);
        return sentByU1 || sentByU2;
    }

    // 친구 요청을 생성하고 저장합니다.
    @Transactional
    public FriendRequest createRequest(Users requester, Users receiver) {
        // 자기 자신에게 친구 요청을 보내는 것을 차단합니다.
        if (requester.getUserId().equals(receiver.getUserId())) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

        // 이미 처리 대기 중인 요청이 있으면 차단합니다.
        if (hasPendingRequest(requester, receiver)) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

        FriendRequest request = FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        return repository.save(request);
    }

    // 요청 단건 조회 (대기 상태가 아니면 에러 반환)
    @Transactional(readOnly = true)
    public FriendRequest getPendingRequest(Long inviteId) {
        FriendRequest request = repository.findById(inviteId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND));

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new FriendException(FriendErrorCode.FRIENDSHIP_NOT_FOUND);
        }
        return request;
    }

    // 내가 보낸 대기 중인 요청 목록
    @Transactional(readOnly = true)
    public List<FriendRequest> getSentRequests(Users requester) {
        return repository.findAllByRequesterAndStatus(requester, FriendRequestStatus.PENDING);
    }

    // 내가 받은 대기 중인 요청 목록
    @Transactional(readOnly = true)
    public List<FriendRequest> getReceivedRequests(Users receiver) {
        return repository.findAllByReceiverAndStatus(receiver, FriendRequestStatus.PENDING);
    }
}