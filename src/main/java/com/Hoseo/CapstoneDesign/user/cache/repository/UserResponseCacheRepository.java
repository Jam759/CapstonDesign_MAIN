package com.Hoseo.CapstoneDesign.user.cache.repository;

import com.Hoseo.CapstoneDesign.global.cache.RedisJsonCacheRepository;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserResponseCacheRepository {

    private static final String MY_INFO_KEY_PREFIX = "user:me:";

    private final RedisJsonCacheRepository redisJsonCacheRepository;

    public Optional<MyInfoResponse> findMyInfo(Long userId) {
        return redisJsonCacheRepository.get(myInfoKey(userId), MyInfoResponse.class);
    }

    public void saveMyInfo(Long userId, MyInfoResponse response, Duration ttl) {
        redisJsonCacheRepository.set(myInfoKey(userId), response, ttl);
    }

    public void deleteMyInfo(Long userId) {
        redisJsonCacheRepository.delete(myInfoKey(userId));
    }

    private String myInfoKey(Long userId) {
        return MY_INFO_KEY_PREFIX + userId;
    }
}
