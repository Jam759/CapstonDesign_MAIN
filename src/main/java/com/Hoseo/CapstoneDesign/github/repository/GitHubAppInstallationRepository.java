package com.Hoseo.CapstoneDesign.github.repository;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GitHubAppInstallationRepository extends JpaRepository<GithubAppInstallations, Long> {

}
