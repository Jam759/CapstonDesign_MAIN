package com.Hoseo.CapstoneDesign.project.cache.service;

import com.Hoseo.CapstoneDesign.global.cache.CacheOperationException;
import com.Hoseo.CapstoneDesign.project.cache.repository.ProjectResponseCacheRepository;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectResponseCacheService {

    private static final Duration MY_PROJECTS_TTL = Duration.ofSeconds(60);

    private final ProjectResponseCacheRepository repository;

    public Optional<List<ProjectThumbnailResponse>> findMyProjects(Long userId) {
        try {
            return repository.findMyProjects(userId);
        } catch (DataAccessException | CacheOperationException ignored) {
            return Optional.empty();
        }
    }

    public void saveMyProjects(Long userId, List<ProjectThumbnailResponse> response) {
        try {
            repository.saveMyProjects(userId, response, MY_PROJECTS_TTL);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }

    public void evictMyProjects(Long userId) {
        try {
            repository.deleteMyProjects(userId);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }
}
