package com.Hoseo.CapstoneDesign.library.cache.repository;

import com.Hoseo.CapstoneDesign.global.cache.RedisJsonCacheRepository;
import com.Hoseo.CapstoneDesign.library.dto.application.LearningRecommendationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LearningRecommendationCacheRepository {

    private static final String KEY_PREFIX = "learning-recommendation:";

    private final RedisJsonCacheRepository redisJsonCacheRepository;

    public Optional<List<LearningRecommendationItem>> find(String platform, Long jobId) {
        return redisJsonCacheRepository.getList(key(platform, jobId), LearningRecommendationItem.class);
    }

    public void save(String platform, Long jobId, List<LearningRecommendationItem> items, Duration ttl) {
        redisJsonCacheRepository.set(key(platform, jobId), items, ttl);
    }

    private String key(String platform, Long jobId) {
        return KEY_PREFIX + platform + ":job:" + jobId;
    }
}
