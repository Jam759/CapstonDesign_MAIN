package com.Hoseo.CapstoneDesign.project.repository;

import com.Hoseo.CapstoneDesign.project.entity.ProjectTechStack;
import com.Hoseo.CapstoneDesign.project.entity.compositeKey.ProjectTechStackId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTechStackRepository extends JpaRepository<ProjectTechStack, ProjectTechStackId> {
}
