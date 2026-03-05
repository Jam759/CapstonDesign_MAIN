package com.Hoseo.CapstoneDesign.auth.facade;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public interface AuthFacade {
    TokenPair accessTokenReissue(String rawRefreshToken);

    void logout(Users user, String rawAccessToken, String rawRefreshToken);
}
