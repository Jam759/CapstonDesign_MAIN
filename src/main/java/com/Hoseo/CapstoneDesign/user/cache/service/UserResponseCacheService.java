package com.Hoseo.CapstoneDesign.user.cache.service;

import com.Hoseo.CapstoneDesign.global.cache.CacheOperationException;
import com.Hoseo.CapstoneDesign.user.cache.repository.UserResponseCacheRepository;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserResponseCacheService {

    private static final Duration MY_INFO_TTL = Duration.ofSeconds(30);

    private final UserResponseCacheRepository repository;

    public Optional<MyInfoResponse> findMyInfo(Long userId) {
        try {
            return repository.findMyInfo(userId);
        } catch (DataAccessException | CacheOperationException ignored) {
            return Optional.empty();
        }
    }

    public void saveMyInfo(Long userId, MyInfoResponse response) {
        try {
            repository.saveMyInfo(userId, response, MY_INFO_TTL);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }

    public void evictMyInfo(Long userId) {
        try {
            repository.deleteMyInfo(userId);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }
}
