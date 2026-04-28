package com.Hoseo.CapstoneDesign.analysis.cache.service;

import com.Hoseo.CapstoneDesign.analysis.cache.repository.ProjectAnalysisUserViewCacheRepository;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.global.cache.CacheOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectAnalysisUserViewCacheService {

    private static final Duration LATEST_TTL = Duration.ofMinutes(10);
    private static final Duration VERSION_TTL = Duration.ofHours(1);

    private final ProjectAnalysisUserViewCacheRepository repository;

    public Optional<ProjectAnalysisUserViewResponse> findUserView(Long projectId, Integer version) {
        try {
            return repository.find(projectId, version);
        } catch (DataAccessException | CacheOperationException ignored) {
            return Optional.empty();
        }
    }

    public void saveUserView(
            Long projectId,
            Integer requestedVersion,
            Integer actualVersion,
            ProjectAnalysisUserViewResponse response
    ) {
        try {
            repository.save(projectId, requestedVersion, response, ttl(requestedVersion));

            if (requestedVersion == null && actualVersion != null) {
                repository.save(projectId, actualVersion, response, VERSION_TTL);
            }
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }

    public void evictUserView(Long projectId, Integer version) {
        try {
            repository.deleteUserView(projectId, version);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }

    private Duration ttl(Integer version) {
        return version == null ? LATEST_TTL : VERSION_TTL;
    }
}
