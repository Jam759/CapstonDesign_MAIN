package com.Hoseo.CapstoneDesign.github.entity;

import com.Hoseo.CapstoneDesign.github.dto.application.GithubInstallationDetailResponse;
import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class GithubAppInstallations extends CreatableEntity {

    @Id
    @Column(name = "github_app_installations_id")
    private Long githubAppInstallationsId;

    @Column(name = "account_id", nullable = false,unique = true)
    private Long accountId;

    @Column(name = "account_login", nullable = false)
    private String accountLogin;

    public void refreshFrom(Long accountId,String accountLogin) {
        this.accountLogin = accountLogin;
        this.accountId =accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GithubAppInstallations that)) return false;
        return Objects.equals(githubAppInstallationsId, that.githubAppInstallationsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(githubAppInstallationsId);
    }

}
