package com.Hoseo.CapstoneDesign.project.repository;

import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectsRepository extends JpaRepository<Projects, Long> {
    Optional<Projects> findByInstallationRepository(InstallationRepository installationRepository);
}
