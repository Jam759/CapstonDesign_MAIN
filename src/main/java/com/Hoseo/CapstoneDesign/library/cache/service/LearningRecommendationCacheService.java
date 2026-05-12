package com.Hoseo.CapstoneDesign.library.cache.service;

import com.Hoseo.CapstoneDesign.global.cache.CacheOperationException;
import com.Hoseo.CapstoneDesign.library.cache.repository.LearningRecommendationCacheRepository;
import com.Hoseo.CapstoneDesign.library.dto.application.LearningRecommendationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LearningRecommendationCacheService {

    private static final Duration TTL = Duration.ofHours(6);

    private final LearningRecommendationCacheRepository repository;

    public Optional<List<LearningRecommendationItem>> find(String platform, Long jobId) {
        try {
            return repository.find(platform, jobId);
        } catch (DataAccessException | CacheOperationException ignored) {
            return Optional.empty();
        }
    }

    public void save(String platform, Long jobId, List<LearningRecommendationItem> items) {
        try {
            repository.save(platform, jobId, items, TTL);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }
}
