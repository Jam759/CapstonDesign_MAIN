package com.Hoseo.CapstoneDesign.auth.facade;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;

public interface AuthFacade {
    TokenPair accessTokenReissue(String rawRefreshToken);
}
