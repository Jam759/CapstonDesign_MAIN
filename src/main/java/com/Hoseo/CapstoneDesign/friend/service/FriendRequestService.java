package com.Hoseo.CapstoneDesign.friend.service;

import com.Hoseo.CapstoneDesign.friend.repository.FriendRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository repository;

}
