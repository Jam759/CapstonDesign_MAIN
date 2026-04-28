package com.Hoseo.CapstoneDesign.security.repository;

import com.Hoseo.CapstoneDesign.security.entity.AccessTokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AccessTokenBlackListRepository extends JpaRepository<AccessTokenBlackList, UUID> {
    boolean existsByJtiAndExpiresAtAfter(UUID jti, LocalDateTime now);

    Optional<AccessTokenBlackList> findByJtiAndExpiresAtAfter(UUID jti, LocalDateTime now);

    @Modifying
    @Query("delete from AccessTokenBlackList blacklist where blacklist.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}
