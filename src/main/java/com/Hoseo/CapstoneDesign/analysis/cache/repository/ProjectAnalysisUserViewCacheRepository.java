package com.Hoseo.CapstoneDesign.analysis.cache.repository;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.global.cache.RedisJsonCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectAnalysisUserViewCacheRepository {

    private static final String KEY_PREFIX = "analysis:user-view:";

    private final RedisJsonCacheRepository redisJsonCacheRepository;

    public Optional<ProjectAnalysisUserViewResponse> find(Long projectId, Integer version) {
        return redisJsonCacheRepository.get(key(projectId, version), ProjectAnalysisUserViewResponse.class);
    }

    public void save(Long projectId, Integer version, ProjectAnalysisUserViewResponse response, Duration ttl) {
        redisJsonCacheRepository.set(key(projectId, version), response, ttl);
    }

    public void delete(Long projectId, Integer version) {
        redisJsonCacheRepository.delete(key(projectId, version));
    }

    public void deleteUserView(Long projectId, Integer version) {
        redisJsonCacheRepository.deleteAll(List.of(key(projectId, null), key(projectId, version)));
    }

    private String key(Long projectId, Integer version) {
        return KEY_PREFIX + projectId + ":" + (version == null ? "latest" : "version:" + version);
    }
}
