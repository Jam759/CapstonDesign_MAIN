package com.Hoseo.CapstoneDesign.project.factory;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.ProjectTechStack;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberRole;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectStatus;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.time.LocalTime;
import java.util.List;

public class ProjectEntityFactory {

    public static Projects toProjects(ProjectCreateRequest request, Users user) {
        //간단한 검증(엔티티 제약 기준)

        return Projects.builder()
                .user(user)
                .title(request.name())
                .description(request.description())
                .goal(request.goal())
                .startDate(request.startDate() == null ? null : request.startDate().atStartOfDay())
                .endDate(request.endDate() == null ? null : request.endDate().atTime(LocalTime.MAX))
                .projectStatus(ProjectStatus.REPO_NOT_CONNECTED)
                .build();
    }

    public static ProjectMember toProjectsMember(Users user, Projects project, ProjectMemberRole role, ProjectInviteStatus status) {
        return toProjectsMember(user, project, role, status, null);
    }

    public static ProjectMember toProjectsMember(
            Users user,
            Projects project,
            ProjectMemberRole role,
            ProjectInviteStatus status,
            CommonGroupDetail projectPositionCmd
    ) {
        return ProjectMember.builder()
                .projectRole(role)
                .project(project)
                .user(user)
                .projectPositionCmd(projectPositionCmd)
                .response(status)
                .responseAt(role == ProjectMemberRole.OWNER ? project.getCreatedAt() : null)
                .invitedBy(null)
                .build();
    }

    public static ProjectMember toInvitedMember(Users invitedUser, Projects project, Users invitedBy) {
        return ProjectMember.builder()
                .projectRole(ProjectMemberRole.MEMBER)
                .project(project)
                .user(invitedUser)
                .projectPositionCmd(null)
                .response(ProjectInviteStatus.INVITED)
                .responseAt(null)
                .invitedBy(invitedBy)
                .build();
    }

    public static List<ProjectTechStack> toProjectTechStackList(
            Projects savedPj,
            List<CommonGroupDetail> techStacks
    ) {
        return techStacks.stream()
                .map(techStack -> ProjectTechStack.builder()
                        .project(savedPj)
                        .projectTechStack(techStack)
                        .build())
                .toList();
    }
}
