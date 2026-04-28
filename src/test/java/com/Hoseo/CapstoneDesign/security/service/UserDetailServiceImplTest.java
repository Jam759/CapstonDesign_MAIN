package com.Hoseo.CapstoneDesign.security.service;

import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.security.cache.factory.AuthenticatedUserCacheFactory;
import com.Hoseo.CapstoneDesign.security.cache.service.AuthenticatedUserCacheService;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.Hoseo.CapstoneDesign.security.service.impl.UserDetailServiceImpl;
import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailServiceImplTest {

    private UserService userService;
    private AuthenticatedUserCacheService authenticatedUserCacheService;
    private UserDetailServiceImpl service;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        authenticatedUserCacheService = mock(AuthenticatedUserCacheService.class);
        service = new UserDetailServiceImpl(userService, authenticatedUserCacheService);
    }

    @Test
    @DisplayName("loads user details from cache without DB lookup")
    void loadsUserDetailsFromCache() {
        UUID identityId = UUID.randomUUID();
        Users cachedUser = UsersTestBuilder.defaultUser()
                .userId(1L)
                .identityId(identityId)
                .build();

        AuthenticatedUserCacheEntry cachedEntry = AuthenticatedUserCacheFactory.fromUser(cachedUser);
        when(authenticatedUserCacheService.findByIdentityId(identityId)).thenReturn(Optional.of(cachedEntry));

        UserDetails result = service.loadUserByUsername(identityId.toString());

        assertThat(result).isInstanceOf(UserDetailImpl.class);
        assertThat(((UserDetailImpl) result).getAuthenticatedUser()).isEqualTo(cachedEntry);
        verify(userService, never()).getByIdentityId(identityId);
    }

    @Test
    @DisplayName("loads user details from DB and saves cache on miss")
    void loadsUserDetailsFromDbAndSavesCacheOnMiss() {
        UUID identityId = UUID.randomUUID();
        Users user = UsersTestBuilder.defaultUser()
                .userId(1L)
                .identityId(identityId)
                .build();

        when(authenticatedUserCacheService.findByIdentityId(identityId)).thenReturn(Optional.empty());
        when(userService.getByIdentityId(identityId)).thenReturn(user);

        UserDetails result = service.loadUserByUsername(identityId.toString());

        assertThat(((UserDetailImpl) result).getUserId()).isEqualTo(user.getUserId());
        verify(authenticatedUserCacheService).save(user);
    }
}
