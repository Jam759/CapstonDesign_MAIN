package com.Hoseo.CapstoneDesign.auth.facade;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;

public interface AuthFacade {
    TokenPair accessTokenReissue(String rawRefreshToken);

    void logout(AuthenticatedUserCacheEntry user, String rawAccessToken, String rawRefreshToken);
}
