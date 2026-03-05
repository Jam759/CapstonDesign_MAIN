package com.Hoseo.CapstoneDesign.security.repository;

import com.Hoseo.CapstoneDesign.security.entity.RefreshToken;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update RefreshToken rt
           set rt.revokedAt = :now
           where rt.user = :user and rt.revokedAt is null
           """)
    int revokeAllActiveByUser(Users user, LocalDateTime now);

}

