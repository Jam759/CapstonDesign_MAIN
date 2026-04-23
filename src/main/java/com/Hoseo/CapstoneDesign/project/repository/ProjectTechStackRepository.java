package com.Hoseo.CapstoneDesign.project.repository;

import com.Hoseo.CapstoneDesign.project.entity.ProjectTechStack;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.compositeKey.ProjectTechStackId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTechStackRepository extends JpaRepository<ProjectTechStack, ProjectTechStackId> {
    List<ProjectTechStack> findByProject(Projects project);
}
