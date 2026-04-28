package com.Hoseo.CapstoneDesign.security.entity;

import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserDetailImpl implements UserDetails {

    private final AuthenticatedUserCacheEntry user;

    public UserDetailImpl(AuthenticatedUserCacheEntry user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.userId();
    }

    public UUID getIdentityId() {
        return user.identityId();
    }

    public SystemRole getSystemRole() {
        return user.systemRole();
    }

    public AuthenticatedUserCacheEntry getAuthenticatedUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.systemRole().toString()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.identityId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

