package com.Hoseo.CapstoneDesign.project.cache.repository;

import com.Hoseo.CapstoneDesign.global.cache.RedisJsonCacheRepository;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectResponseCacheRepository {

    private static final String MY_PROJECT_KEY_PREFIX = "project:my-list:user:";

    private final RedisJsonCacheRepository redisJsonCacheRepository;

    public Optional<List<ProjectThumbnailResponse>> findMyProjects(Long userId) {
        return redisJsonCacheRepository.getList(myProjectsKey(userId), ProjectThumbnailResponse.class);
    }

    public void saveMyProjects(Long userId, List<ProjectThumbnailResponse> response, Duration ttl) {
        redisJsonCacheRepository.set(myProjectsKey(userId), response, ttl);
    }

    public void deleteMyProjects(Long userId) {
        redisJsonCacheRepository.delete(myProjectsKey(userId));
    }

    private String myProjectsKey(Long userId) {
        return MY_PROJECT_KEY_PREFIX + userId;
    }
}
