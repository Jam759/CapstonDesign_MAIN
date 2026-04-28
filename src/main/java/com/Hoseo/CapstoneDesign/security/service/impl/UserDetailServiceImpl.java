package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.security.cache.service.AuthenticatedUserCacheService;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilException;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserService service;
    private final AuthenticatedUserCacheService authenticatedUserCacheService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UUID identityId = parseIdentityId(username);
        return authenticatedUserCacheService.findByIdentityId(identityId)
                .map(UserDetailImpl::new)
                .orElseGet(() -> loadFromDb(identityId));
    }

    private UUID parseIdentityId(String username) {
        try {
            return UUID.fromString(username);
        } catch (IllegalArgumentException e) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        }
    }

    private UserDetailImpl loadFromDb(UUID identityId) {
        try {
            Users member = service.getByIdentityId(identityId);
            AuthenticatedUserCacheEntry entry = authenticatedUserCacheService.save(member);
            return new UserDetailImpl(entry);
        } catch (CustomUserException e) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        }
    }
}
