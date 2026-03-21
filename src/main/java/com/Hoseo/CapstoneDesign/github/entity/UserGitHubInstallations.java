package com.Hoseo.CapstoneDesign.github.entity;

import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserGitHubInstallations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userGitHubInstallationsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "github_app_installation_id")
    private GithubAppInstallations githubAppInstallation;
}
