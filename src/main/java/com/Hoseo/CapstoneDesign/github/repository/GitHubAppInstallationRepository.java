package com.Hoseo.CapstoneDesign.github.repository;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GitHubAppInstallationRepository extends JpaRepository<GithubAppInstallations, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        INSERT INTO github_app_installations
            (github_app_installations_id, account_id, account_login, created_at)
        VALUES
            (:installationId, :accountId, :accountLogin, NOW())
        ON DUPLICATE KEY UPDATE
            account_id = VALUES(account_id),
            account_login = VALUES(account_login)
        """, nativeQuery = true)
    int upsertInstallation(
            @Param("installationId") Long installationId,
            @Param("accountId") Long accountId,
            @Param("accountLogin") String accountLogin
    );
}
