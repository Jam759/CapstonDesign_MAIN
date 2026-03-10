package com.Hoseo.CapstoneDesign.auth.facade.impl;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.auth.facade.AuthFacade;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.security.factory.SecurityDtoFactory;
import com.Hoseo.CapstoneDesign.security.service.AccessTokenBlackListService;
import com.Hoseo.CapstoneDesign.security.service.RefreshTokenService;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilException;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenBlackListService accessTokenBlackListService;

    @Override
    @Transactional(readOnly = false)
    public TokenPair accessTokenReissue(String rawRefreshToken) {
        Users user = userService.getByIdentityId(jwtUtil.getSubjectFromRefreshToken(rawRefreshToken));
        String newRawRefreshToken = refreshTokenService.rotate(user, rawRefreshToken);
        String newRawAccessToken = jwtUtil.createAccessToken(user);
        return SecurityDtoFactory.toTokenPair(newRawAccessToken, newRawRefreshToken, jwtUtil, user);
    }

    @Override
    @Transactional(readOnly = false)
    public void logout(Users user, String rawAccessToken, String rawRefreshToken) {
        if (rawAccessToken == null || rawAccessToken.isBlank()) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_IS_NULL);
        }
        accessTokenBlackListService.saveBlackList(rawAccessToken, user);
        refreshTokenService.revokeAndSoftDeleteByFamily(user, rawRefreshToken);
    }

}
