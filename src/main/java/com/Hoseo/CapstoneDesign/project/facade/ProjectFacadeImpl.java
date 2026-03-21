package com.Hoseo.CapstoneDesign.project.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberRole;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.factory.ProjectDtoFactory;
import com.Hoseo.CapstoneDesign.project.factory.ProjectEntityFactory;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class ProjectFacadeImpl implements ProjectFacade{

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

    @Override
    @Transactional(readOnly = false)
    public void createProject(ProjectCreateRequest request, Users user) {
        Projects pjEntity = ProjectEntityFactory.toProjects(request,user);
        Projects savedPj = projectService.create(pjEntity);

        ProjectMember projectOwner =
                ProjectEntityFactory.toProjectsMember(
                        user,savedPj,
                        ProjectMemberRole.OWNER,
                        ProjectInviteStatus.ACCEPTED
                );
        projectMemberService.create(projectOwner);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectSettingResponse getProjectSetting(Long projectId, Users user) {
        Projects p = projectService.getById(projectId);
        if (!p.getUser().equals(user))
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        return ProjectDtoFactory.toProjectSettingResponse(p);
    }

    @Override
    public ProjectSettingResponse updateProject(Long projectId, Users user, ProjectSettingRequest request) {
        Projects p = projectService.getById(projectId);
        if (!p.getUser().equals(user))
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);

        // TODO: 여기서 브런치, 리포 id 등등 세팅 할것

        return null;
    }
}
