package com.Hoseo.CapstoneDesign.project.repository;

import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
}
