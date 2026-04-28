package com.Hoseo.CapstoneDesign.security.cache.service;

import com.Hoseo.CapstoneDesign.global.cache.CacheOperationException;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.security.cache.factory.AuthenticatedUserCacheFactory;
import com.Hoseo.CapstoneDesign.security.cache.repository.AuthenticatedUserCacheRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserCacheService {

    private static final Duration AUTHENTICATED_USER_TTL = Duration.ofSeconds(60);

    private final AuthenticatedUserCacheRepository repository;

    public Optional<AuthenticatedUserCacheEntry> findByIdentityId(UUID identityId) {
        try {
            return repository.findByIdentityId(identityId);
        } catch (DataAccessException | CacheOperationException ignored) {
            return Optional.empty();
        }
    }

    public AuthenticatedUserCacheEntry save(Users user) {
        AuthenticatedUserCacheEntry entry = AuthenticatedUserCacheFactory.fromUser(user);
        try {
            repository.save(entry, AUTHENTICATED_USER_TTL);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
        return entry;
    }

    public void evict(UUID identityId) {
        try {
            repository.delete(identityId);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }
}
