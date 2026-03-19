package com.Hoseo.CapstoneDesign.project.factory;

import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberRole;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectStatus;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public class ProjectEntityFactory {

    public static Projects toProjects(ProjectCreateRequest request, Users user) {
        if(user == null
            || request.projectType() == null
            || request.projectTitle() == null
            || request.description() == null
        ) throw new IllegalArgumentException();

        return Projects.builder()
                .user(user)
                .projectType(request.projectType())
                .title(request.projectTitle())
                .description(request.description())
                .goal("")
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
}
