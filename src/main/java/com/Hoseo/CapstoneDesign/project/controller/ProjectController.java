package com.Hoseo.CapstoneDesign.project.controller;

import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteResponseRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.InviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.facade.ProjectFacade;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping()
    public ResponseEntity<List<ProjectThumbnailResponse>> getMyProject(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
//        List<ProjectThumbnailResponse> res = facade.getMyProject(userDetail.getUser());
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @PathVariable Long projectId
    ) {
//        facade.deleteProject(userDetail.getUser(), projectId);
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

    //초대하기
    @PostMapping("/members")
    public ResponseEntity<ProjectInviteResponse> inviteProject(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectInviteRequest request
    ){
//        ProjectInviteResponse res = facade.inviteProject(userDetail.getUser(),request);
        return ResponseEntity.ok(res);
    }

    //초대 응답
    @PatchMapping("/member")
    public ResponseEntity<ProjectInviteResponse> responseInvite(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectInviteResponseRequest request
    ){
//        ProjectInviteResponse res = facade.responseInvite(userDetail.getUser(), request);
        return ResponseEntity.ok(res);
    }

    //초대 받은 리스트 확인
    @GetMapping("member")
    public ResponseEntity<List<InviteStatusResponse>> getMyInvitedList(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ){
//        List<InviteStatusResponse> res = facade.getMyInvited(userDetail.getUser());
        return ResponseEntity.ok(res);
    }

}
