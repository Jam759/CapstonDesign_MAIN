package com.Hoseo.CapstoneDesign.security.factory;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityDtoFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(SecurityDtoFactoryTest.class);

    @Test
    @DisplayName("toTokenPair keeps profile setup and github installation flags")
    void toTokenPairKeepsStatusFlags() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        Date now = new Date();

        when(jwtUtil.getExpirationFromAccessToken("access-token")).thenReturn(now);
        when(jwtUtil.getExpirationFromRefreshToken("refresh-token")).thenReturn(now);

        TokenPair tokenPair = SecurityDtoFactory.toTokenPair(
                "access-token",
                "refresh-token",
                jwtUtil,
                true,
                false
        );

        assertThat(tokenPair.needsProfileSetup()).isTrue();
        assertThat(tokenPair.githubInstalled()).isFalse();
        log.info("[TEST] token pair status flags preserved");
    }
}
