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

import java.util.List;

public class ProjectEntityFactory {

    public static Projects toProjects(ProjectCreateRequest request, Users user) {
        //간단한 검증(엔티티 제약 기준)

        return Projects.builder()
                .user(user)
                .title(request.projectTitle())
                .description(request.description())
                .goal(request.goal())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .projectStatus(ProjectStatus.REPO_NOT_CONNECTED)
                .build();
    }

    public static ProjectMember toProjectsMember(Users user, Projects project, ProjectMemberRole role, ProjectInviteStatus status) {
        return ProjectMember.builder()
                .projectRole(role)
                .project(project)
                .user(user)
                .response(status)
                .responseAt(role == ProjectMemberRole.OWNER ? project.getCreatedAt() : null)
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
