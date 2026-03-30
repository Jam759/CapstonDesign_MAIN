package com.Hoseo.CapstoneDesign.project.repository;

import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Projects project);

    boolean existsByProjectProjectIdAndUserUserIdAndResponse(
            Long projectId,
            Long userId,
            ProjectInviteStatus response
    );
}
