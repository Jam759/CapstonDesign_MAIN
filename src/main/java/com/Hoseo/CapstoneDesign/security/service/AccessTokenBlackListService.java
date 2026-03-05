package com.Hoseo.CapstoneDesign.security.service;

import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.Optional;

public interface AccessTokenBlackListService {
    AccessTokenBlackListCache getBlackList(String accessToken);

    Optional<AccessTokenBlackListCache> findBlackList(String accessToken);

    void saveBlackList(String accessToken, Users user);

    boolean isExistByToken(String accessToken);
}
