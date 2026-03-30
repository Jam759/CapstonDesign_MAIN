package com.Hoseo.CapstoneDesign.project.controller;

import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteResponseRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.InviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
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
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<ProjectThumbnailResponse> res = facade.getMyProject(userDetail.getUser());

        List<ProjectThumbnailResponse> res = List.of(
                ProjectThumbnailResponse.builder()
                        .projectId(101L)
                        .title("알고리즘 스터디")
                        .description("백준 풀이 기록과 회고를 관리하는 프로젝트")
                        .build(),
                ProjectThumbnailResponse.builder()
                        .projectId(102L)
                        .title("캡스톤 디자인")
                        .description("GitHub 분석 기반 협업 보조 서비스 메인 프로젝트")
                        .build(),
                ProjectThumbnailResponse.builder()
                        .projectId(103L)
                        .title("TypeScript 연습장")
                        .description("언어 학습용 미니 미션과 실습 코드를 모아둔 프로젝트")
                        .build()
        );
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @PathVariable Long projectId
    ) {

        // TODO : 추후 구현 현재는 mock데이터 반환
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
        // TODO : 추후 구현 현재는 mock데이터 반환
//        ProjectInviteResponse res = facade.inviteProject(userDetail.getUser(),request);

        Long invitedUserId = request.getInviteMemberId() != null ? request.getInviteMemberId() : 2001L;
        ProjectInviteResponse res = new ProjectInviteResponse(
                mockProjectMemberId(request.getProjectId(), invitedUserId),
                invitedUserId,
                ProjectInviteStatus.INVITED
        );
        return ResponseEntity.ok(res);
    }

    //초대 응답
    @PatchMapping("/member")
    public ResponseEntity<ProjectInviteResponse> responseInvite(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectInviteResponseRequest request
    ){
        // TODO : 추후 구현 현재는 mock데이터 반환
//        ProjectInviteResponse res = facade.responseInvite(userDetail.getUser(), request);

        Long invitedUserId = resolveUserId(userDetail);
        ProjectInviteStatus status = request.getResponseStatus() != null
                ? request.getResponseStatus()
                : ProjectInviteStatus.ACCEPTED;
        ProjectInviteResponse res = new ProjectInviteResponse(
                mockProjectMemberId(request.getProjectId(), invitedUserId),
                invitedUserId,
                status
        );
        return ResponseEntity.ok(res);
    }

    //초대 받은 리스트 확인
    @GetMapping("member")
    public ResponseEntity<List<InviteStatusResponse>> getMyInvitedList(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ){
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<InviteStatusResponse> res = facade.getMyInvited(userDetail.getUser());

        List<InviteStatusResponse> res = List.of(
                InviteStatusResponse.builder()
                        .projectId(201L)
                        .status(ProjectInviteStatus.INVITED)
                        .build(),
                InviteStatusResponse.builder()
                        .projectId(202L)
                        .status(ProjectInviteStatus.ACCEPTED)
                        .build(),
                InviteStatusResponse.builder()
                        .projectId(203L)
                        .status(ProjectInviteStatus.DECLINED)
                        .build()
        );
        return ResponseEntity.ok(res);
    }

    private Long mockProjectMemberId(Long projectId, Long userId) {
        long resolvedProjectId = projectId != null ? projectId : 999L;
        long resolvedUserId = userId != null ? userId : 2001L;
        return resolvedProjectId * 100 + resolvedUserId;
    }

    private Long resolveUserId(UserDetailImpl userDetail) {
        if (userDetail == null || userDetail.getUser() == null || userDetail.getUser().getUserId() == null) {
            return 2001L;
        }
        return userDetail.getUser().getUserId();
    }

}
