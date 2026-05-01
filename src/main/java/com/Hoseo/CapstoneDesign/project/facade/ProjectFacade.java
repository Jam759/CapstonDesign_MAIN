package com.Hoseo.CapstoneDesign.project.facade;

import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteResponseRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectCreateResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;

import java.util.List;

public interface ProjectFacade {
    ProjectCreateResponse createProject(ProjectCreateRequest request, AuthenticatedUserCacheEntry user);

    List<ProjectThumbnailResponse> getMyProject(AuthenticatedUserCacheEntry user);

    ProjectSettingResponse getProjectSetting(Long projectId, AuthenticatedUserCacheEntry user);

    ProjectSettingResponse updateProject(Long projectId, AuthenticatedUserCacheEntry user, ProjectSettingRequest request);

    void deleteProject(Long projectId, AuthenticatedUserCacheEntry user);

    List<ProjectInviteResponse> inviteProject(ProjectInviteRequest request, AuthenticatedUserCacheEntry user);

    ProjectInviteResponse responseInvite(ProjectInviteResponseRequest request, AuthenticatedUserCacheEntry user);

    List<ProjectInviteStatusResponse> getMyInvitedList(AuthenticatedUserCacheEntry user);
}
