package com.Hoseo.CapstoneDesign.project.controller;

import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.facade.ProjectFacade;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
