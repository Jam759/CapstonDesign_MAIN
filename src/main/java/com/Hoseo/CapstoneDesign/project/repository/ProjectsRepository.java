package com.Hoseo.CapstoneDesign.project.repository;

import com.Hoseo.CapstoneDesign.project.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectsRepository extends JpaRepository<Projects, Long> {
}
