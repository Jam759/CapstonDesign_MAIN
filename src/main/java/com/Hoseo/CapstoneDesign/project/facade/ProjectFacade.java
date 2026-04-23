package com.Hoseo.CapstoneDesign.project.facade;

import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteResponseRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.InviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectCreateResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.List;

public interface ProjectFacade {
    ProjectCreateResponse createProject(ProjectCreateRequest request, Users user);

    List<ProjectThumbnailResponse> getMyProject(Users user);

    ProjectSettingResponse getProjectSetting(Long projectId, Users user);

    ProjectSettingResponse updateProject(Long projectId, Users user, ProjectSettingRequest request);

    void deleteProject(Long projectId, Users user);

    List<ProjectInviteResponse> inviteProject(ProjectInviteRequest request, Users user);

    ProjectInviteResponse responseInvite(ProjectInviteResponseRequest request, Users user);

    List<InviteStatusResponse> getMyInvitedList(Users user);
}
