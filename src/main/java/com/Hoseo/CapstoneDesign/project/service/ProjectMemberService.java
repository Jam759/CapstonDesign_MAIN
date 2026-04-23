package com.Hoseo.CapstoneDesign.project.service;

import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberRole;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository repository;

    public ProjectMember create(ProjectMember projectMember) {
        return repository.save(projectMember);
    }

    public List<ProjectMember> createAll(List<ProjectMember> projectMembers) {
        return repository.saveAll(projectMembers);
    }

    public List<ProjectMember> getProjectMember(Projects p) {
        return repository.findByProject(p);
    }

    public List<ProjectMember> getAcceptedMembersByUserId(Long userId) {
        return repository.findByUserUserIdAndResponse(userId, ProjectInviteStatus.ACCEPTED);
    }

    public List<ProjectMember> getMemberInvitesByUserId(Long userId) {
        return repository.findByUserUserIdAndProjectRoleOrderByCreatedAtDesc(
                userId,
                ProjectMemberRole.MEMBER
        );
    }

    public ProjectMember getByIdAndUserId(Long projectMemberId, Long userId) {
        return repository.findByProjectMemberIdAndUserUserId(projectMemberId, userId)
                .orElseThrow(() -> new ProjectsException(ProjectsErrorCode.PROJECT_INVITE_NOT_FOUND));
    }

    public ProjectMember getPendingInvite(Long projectId, Long userId) {
        return repository.findByProjectProjectIdAndUserUserIdAndResponse(
                        projectId,
                        userId,
                        ProjectInviteStatus.INVITED
                )
                .orElseThrow(() -> new ProjectsException(ProjectsErrorCode.PROJECT_INVITE_NOT_FOUND));
    }

    public boolean isAcceptedMember(Long projectId, Long userId) {
        return repository.existsByProjectProjectIdAndUserUserIdAndResponse(
                projectId,
                userId,
                ProjectInviteStatus.ACCEPTED
        );
    }

    public boolean isOwnerMember(Long projectId, Long userId) {
        return repository.existsByProjectProjectIdAndUserUserIdAndProjectRoleAndResponse(
                projectId,
                userId,
                ProjectMemberRole.OWNER,
                ProjectInviteStatus.ACCEPTED
        );
    }

    public boolean existsProjectMember(Long projectId, Long userId) {
        return repository.existsByProjectProjectIdAndUserUserId(projectId, userId);
    }
}
