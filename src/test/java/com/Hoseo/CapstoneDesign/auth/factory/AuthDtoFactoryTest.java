package com.Hoseo.CapstoneDesign.auth.factory;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.auth.dto.response.AccessTokenReissueResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AuthDtoFactoryTest {

    @DisplayName("toAccessTokenReissueResponse: TokenPair를 응답 DTO로 변환한다")
    @Test
    void toAccessTokenReissueResponse_success() {
        LocalDateTime accessExp = LocalDateTime.now().plusMinutes(15);
        LocalDateTime refreshExp = LocalDateTime.now().plusDays(7);
        TokenPair tokenPair = new TokenPair("access", "refresh", accessExp, refreshExp, true);

        AccessTokenReissueResponse response = AuthDtoFactory.toAccessTokenReissueResponse(tokenPair);

        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.accessTokenExpiredAt()).isEqualTo(accessExp);
        assertThat(response.refreshTokenExpiredAt()).isEqualTo(refreshExp);
        assertThat(response.isNewUser()).isTrue();
    }
}

