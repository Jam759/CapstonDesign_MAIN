package com.Hoseo.CapstoneDesign.project.controller;

import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.facade.ProjectFacade;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectFacade facade;

    @PostMapping()
    public ResponseEntity<Void> createProject(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectCreateRequest request
    ) {
        facade.createProject(request, userDetail.getUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{projectId}/setting")
    public ResponseEntity<ProjectSettingResponse> getProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        ProjectSettingResponse response
                = facade.getProjectSetting(projectId, userDetail.getUser());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/setting")
    public ResponseEntity<ProjectSettingResponse> updateProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectSettingRequest request
    ) {
        ProjectSettingResponse response
                = facade.updateProject(projectId, userDetail.getUser(), request);
        return ResponseEntity.ok(response);
    }

}
