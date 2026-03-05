package com.Hoseo.CapstoneDesign.auth.factory;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.auth.dto.response.AccessTokenReissueResponse;

public class AuthDtoFactory {

    public static AccessTokenReissueResponse toAccessTokenReissueResponse(TokenPair tokenPair) {
        return new AccessTokenReissueResponse(
                tokenPair.accessToken(),
                tokenPair.accessTokenExpiredAt(),
                tokenPair.refreshTokenExpiredAt(),
                tokenPair.isNewUser()
        );
    }
}
