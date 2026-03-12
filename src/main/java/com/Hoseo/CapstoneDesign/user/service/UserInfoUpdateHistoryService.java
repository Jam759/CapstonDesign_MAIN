package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.repository.UserInfoUpdateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoUpdateHistoryService {

    private final UserInfoUpdateHistoryRepository repository;

    public UserInfoUpdateHistory save(UserInfoUpdateHistory history) {
        return repository.save(history);
    }


}
