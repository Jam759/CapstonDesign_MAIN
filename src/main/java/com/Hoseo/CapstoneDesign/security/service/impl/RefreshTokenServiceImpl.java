package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.global.util.UuidUtil;
import com.Hoseo.CapstoneDesign.security.entity.RefreshToken;
import com.Hoseo.CapstoneDesign.security.exception.RefreshTokenErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.RefreshTokenException;
import com.Hoseo.CapstoneDesign.security.factory.SecurityEntityFactory;
import com.Hoseo.CapstoneDesign.security.repository.RefreshTokenRepository;
import com.Hoseo.CapstoneDesign.security.service.RefreshTokenService;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtUtil jwtUtil;

    /**
     * 최초 로그인 시 refresh 발급용.
     * - familyId를 외부에서 넣고 싶으면 파라미터로 받는 오버로드를 써도 됨.
     */
    @Override
    public String createAndSaveInitial(Users user) {
        UUID familyId = UuidUtil.getUuidv7();
        String rawToken = jwtUtil.createRefreshToken(user);
        RefreshToken rt = SecurityEntityFactory.toRefreshToken(jwtUtil, rawToken, familyId, user);
        repository.save(rt);
        return rawToken;
    }

    /**
     * Refresh Rotation:
     * - 정상 사용: old.usedAt 찍고 old.replacedByTokenId 연결 + new 발급/저장
     * - Reuse 감지(usedAt != null): 정책 B(유저 전체 revoke) 적용 후 예외
     */
    @Override
    public String rotate(Users user, String rawOldRefreshToken) {
        LocalDateTime now = TimeUtil.getNowSeoulLocalDateTime();

        String oldTokenHash = DigestUtils.sha256Hex(rawOldRefreshToken);
        RefreshToken old = repository.findByTokenHash(oldTokenHash)
                .orElseThrow(() -> new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 토큰-유저 매칭 (탈취 토큰 방지)
        if (!old.getUser().getUserId().equals(user.getUserId())) {
            throw new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_USER_MISMATCH);
        }
        // 폐기/만료
        if (old.isRevoked()) {
            throw new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_REVOKED);
        }
        if (old.isExpired(now)) {
            throw new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // Reuse 감지: 유저 전체 revoke
        if (old.isUsed()) {
            repository.revokeAllActiveByUser(user, now);
            throw new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_REUSE_DETECTED);
        }
        jwtUtil.validateRefreshToken(rawOldRefreshToken);
        String rawNewRefreshToken = jwtUtil.createRefreshToken(user);
        // 새 토큰 발급 (family 유지)
        RefreshToken next =
                SecurityEntityFactory.toRefreshToken(jwtUtil, rawNewRefreshToken, old.getFamilyId(), user);

        // old 사용 처리 + 체인 연결
        old.markUsed(now, next.getId());

        repository.save(old);
        repository.save(next);

        return rawNewRefreshToken;
    }

    @Override
    public void revokeAndSoftDeleteByFamily(Users user, String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        LocalDateTime now = TimeUtil.getNowSeoulLocalDateTime();
        String tokenHash = DigestUtils.sha256Hex(rawRefreshToken);

        RefreshToken token = repository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!token.getUser().getUserId().equals(user.getUserId())) {
            throw new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_USER_MISMATCH);
        }

        repository.revokeAndSoftDeleteByFamilyId(user, token.getFamilyId(), now);
    }
}
