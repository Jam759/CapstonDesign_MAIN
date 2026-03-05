package com.Hoseo.CapstoneDesign.security.util;

import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilException;
import com.Hoseo.CapstoneDesign.security.properties.JwtProperties;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final JwtParser accessTokenParser;
    private final JwtParser refreshTokenParser;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.accessKey = jwtProperties.accessTokenSecretKey();
        this.refreshKey = jwtProperties.refreshTokenSecretKey();

        this.accessTokenParser = Jwts.parser()
                .requireIssuer(jwtProperties.issuer())
                .clock(() -> Date.from(TimeUtil.getNowSeoulInstant()))
                .clockSkewSeconds(60)
                .verifyWith(accessKey)
                .build();

        this.refreshTokenParser = Jwts.parser()
                .requireIssuer(jwtProperties.issuer())
                .clock(() -> Date.from(TimeUtil.getNowSeoulInstant()))
                .clockSkewSeconds(60)
                .verifyWith(refreshKey)
                .build();
    }

    // ====== 생성 ======
    public String createAccessToken(Users member) {
        return createToken(
                String.valueOf(member.getIdentityId()),
                accessKey,
                jwtProperties.accessTokenTtl()
        );
    }

    public String createRefreshToken(Users member) {
        return createToken(
                String.valueOf(member.getIdentityId()),
                refreshKey,
                jwtProperties.refreshTokenTtl()
        );
    }

    // ====== Claims ======
    public Claims getClaimsFromAccessToken(String token) {
        return parseAccess(token);
    }

    public Claims getClaimsFromRefreshToken(String token) {
        return parseRefresh(token);
    }

    // ====== subject ======
    public UUID getSubjectFromAccessToken(String token) {
        try {
            return UUID.fromString(getClaimsFromAccessToken(token).getSubject());
        } catch (IllegalArgumentException e) {
            log.warn("[JWT] 잘못된 형식의 토큰  {}", e.getMessage());
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        }
    }

    public UUID getSubjectFromRefreshToken(String token) {
        try {
            return UUID.fromString(getClaimsFromRefreshToken(token).getSubject());
        } catch (IllegalArgumentException e) {
            log.warn("[JWT] 잘못된 형식의 토큰  {}", e.getMessage());
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        }
    }

    // ====== jti ======
    public UUID getJtiFromAccessToken(String token) {
        try {
            return UUID.fromString(getClaimsFromAccessToken(token).getId());
        } catch (IllegalArgumentException e) {
            log.warn("[JWT] 잘못된 형식의 토큰  {}", e.getMessage());
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        }
    }

    public UUID getJtiFromRefreshToken(String token) {
        try {
            return UUID.fromString(getClaimsFromRefreshToken(token).getId());
        } catch (IllegalArgumentException e) {
            log.warn("[JWT] 잘못된 형식의 토큰  {}", e.getMessage());
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        }
    }

    // ====== remaining =====
    public String remainingRefreshToken(String refreshToken, Users member) {
        Date refreshTokenExpiration = getExpirationFromRefreshToken(refreshToken);
        LocalDateTime exp = TimeUtil.toLocalDateTime(refreshTokenExpiration);
        LocalDateTime now = TimeUtil.getNowSeoulLocalDateTime();

        Duration remaining = Duration.between(now, exp);
        if (remaining.isNegative() || remaining.isZero()) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_EXPIRED);
        }

        // 남은 시간이 1일(24시간) 이하이면 새 토큰 발급
        if (remaining.compareTo(Duration.ofDays(1)) <= 0) {
            return createRefreshToken(member);
        }
        return null;
    }

    // ====== expiration ======
    public Date getExpirationFromAccessToken(String token) {
        return getClaimsFromAccessToken(token).getExpiration();
    }

    public Date getExpirationFromRefreshToken(String token) {
        return getClaimsFromRefreshToken(token).getExpiration();
    }

    // ====== 검증 ======
    public void validateAccessToken(String token) {
        validateInternal(stripBearerPrefix(token), accessTokenParser);
    }

    public void validateRefreshToken(String token) {
        validateInternal(stripBearerPrefix(token), refreshTokenParser);
    }

    public String resolveTokenFromHttpServletRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    // ====== 내부 구현 ======
    private String createToken(String subject, SecretKey key, Duration ttl) {
        Instant now = TimeUtil.getNowSeoulInstant();
        Instant exp = now.plus(ttl);

        return Jwts.builder()
                .subject(subject)                           // sub
                .id(UUID.randomUUID().toString())           // jti
                .issuer(jwtProperties.issuer())    // iss
                .issuedAt(Date.from(now))                   // iat
                .expiration(Date.from(exp))                 // exp
                .signWith(key, Jwts.SIG.HS256)              // 0.12.x 권장 스타일
                .compact();
    }

    private Claims parseAccess(String rawToken) {
        String token = stripBearerPrefix(rawToken);
        return accessTokenParser.parseSignedClaims(token).getPayload();
    }

    private Claims parseRefresh(String rawToken) {
        String token = stripBearerPrefix(rawToken);
        return refreshTokenParser.parseSignedClaims(token).getPayload();
    }

    public String stripBearerPrefix(String token) {
        if (token == null) return null;
        String t = token.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }

    private void validateInternal(String token, JwtParser parser) {
        try {
            // 토큰 서명/만료/형식 검증 목적: 결과값은 버려도 됨
            parser.parseSignedClaims(Objects.requireNonNull(token));
        } catch (ExpiredJwtException e) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_UNSUPPORTED);
        } catch (SignatureException e) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_BAD_SIGNATURE);
        } catch (MalformedJwtException | io.jsonwebtoken.io.DecodingException e) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_MALFORMED);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        } catch (JwtException e) {
            log.error("[JWT_EXCEPTION] Unknown JWT exception -> {}", e.getMessage(), e);
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_OTHER);
        } catch (Exception e) {
            log.error("[JWT_EXCEPTION] Unknown exception -> {}", e.getMessage(), e);
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_OTHER);
        }
    }
}