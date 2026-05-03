package com.Hoseo.CapstoneDesign.friend.service;

import com.Hoseo.CapstoneDesign.friend.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendShipService {

    private final FriendshipRepository repository;

}
