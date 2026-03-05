package com.Hoseo.CapstoneDesign.security.factory;

import com.Hoseo.CapstoneDesign.security.entity.RefreshToken;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityEntityFactoryTest {

    @DisplayName("toRefreshToken: raw token과 family/user로 RefreshToken 엔티티를 생성한다")
    @Test
    void toRefreshToken_success() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        Users user = Users.builder()
                .userId(1L)
                .identityId(UUID.randomUUID())
                .oauthType(OauthType.GITHUB)
                .oauthProviderId("provider")
                .oauthNickname("nickname")
                .systemRole(SystemRole.USER)
                .build();
        String rawToken = "refresh-token";
        UUID familyId = UUID.randomUUID();
        Date expiration = java.sql.Timestamp.valueOf(LocalDateTime.now().plusDays(7));
        when(jwtUtil.getExpirationFromRefreshToken(rawToken)).thenReturn(expiration);

        RefreshToken result = SecurityEntityFactory.toRefreshToken(jwtUtil, rawToken, familyId, user);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getTokenHash()).isEqualTo(DigestUtils.sha256Hex(rawToken));
        assertThat(result.getExpiresAt()).isNotNull();
    }
}

