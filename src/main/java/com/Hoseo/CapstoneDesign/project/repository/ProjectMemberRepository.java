package com.Hoseo.CapstoneDesign.project.repository;

import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Projects project);

    List<ProjectMember> findByUserUserIdAndResponseAndProjectDeletedAtIsNull(
            Long userId,
            ProjectInviteStatus response
    );

    List<ProjectMember> findByUserUserIdAndProjectRoleAndProjectDeletedAtIsNullOrderByCreatedAtDesc(
            Long userId,
            ProjectMemberRole projectRole
    );

    Optional<ProjectMember> findByProjectMemberIdAndUserUserIdAndProjectDeletedAtIsNull(
            Long projectMemberId,
            Long userId
    );

    Optional<ProjectMember> findByProjectProjectIdAndProjectDeletedAtIsNullAndUserUserIdAndResponse(
            Long projectId,
            Long userId,
            ProjectInviteStatus response
    );

    @Query("SELECT pm.user.userId FROM ProjectMember pm WHERE pm.project.projectId = :projectId")
    List<Long> findUserIdsByProjectId(@Param("projectId") Long projectId);

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
