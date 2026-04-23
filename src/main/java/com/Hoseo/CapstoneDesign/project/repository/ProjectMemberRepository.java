package com.Hoseo.CapstoneDesign.project.repository;

import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Projects project);

    List<ProjectMember> findByUserUserIdAndResponse(
            Long userId,
            ProjectInviteStatus response
    );

    List<ProjectMember> findByUserUserIdAndProjectRoleOrderByCreatedAtDesc(
            Long userId,
            ProjectMemberRole projectRole
    );

    Optional<ProjectMember> findByProjectMemberIdAndUserUserId(Long projectMemberId, Long userId);

    Optional<ProjectMember> findByProjectProjectIdAndUserUserIdAndResponse(
            Long projectId,
            Long userId,
            ProjectInviteStatus response
    );

    boolean existsByProjectProjectIdAndUserUserId(
            Long projectId,
            Long userId
    );

    boolean existsByProjectProjectIdAndUserUserIdAndResponse(
            Long projectId,
            Long userId,
            ProjectInviteStatus response
    );

    boolean existsByProjectProjectIdAndUserUserIdAndProjectRoleAndResponse(
            Long projectId,
            Long userId,
            ProjectMemberRole projectRole,
            ProjectInviteStatus response
    );
}
