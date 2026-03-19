package com.Hoseo.CapstoneDesign.project.facade;

import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public interface ProjectFacade {
    void createProject(ProjectCreateRequest request, Users user);

    ProjectSettingResponse getProjectSetting(Long projectId, Users user);
}
