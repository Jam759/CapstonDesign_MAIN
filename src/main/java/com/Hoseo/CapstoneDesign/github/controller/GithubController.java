package com.Hoseo.CapstoneDesign.github.controller;

import com.Hoseo.CapstoneDesign.github.dto.response.InstallationsAvailableResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryBranchesResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryResponse;
import com.Hoseo.CapstoneDesign.github.facade.GitHubFacade;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/github")
public class GithubController {

    private final GitHubFacade facade;

    //여기서 가입 여부 확인 후 안되어있으면 URL주는 곳으로 가서 이동
    @GetMapping("/installations/available")
    public ResponseEntity<InstallationsAvailableResponse> getInstallAvailable(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam(required = false, defaultValue = "/tmp/oauth2/test") String returnTo//나중에 바꾸기
    ) {
        InstallationsAvailableResponse response =
                facade.getAvailable(userDetail.getUser(), returnTo);
        return ResponseEntity.ok(response);
    }

    //사용자가 깃헙 연결 끝나면 여기로 옴
    @GetMapping("/setup/callback")
    public ResponseEntity<Void> setupCallback(
            @RequestParam("installation_id") Long installationId,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "setup_action", required = false) String setupAction
    ) {

        URI redirectUri =
                facade.connectInstallationIdAndUser(state, installationId, setupAction);

        return ResponseEntity.status(302)
                .location(redirectUri) //redirect보낼 프론트 url -> 프로젝트 메인으로 보내기
                .build();
    }

    //웹훅
    @PostMapping("/webhook/callback")
    public ResponseEntity<Void> githupWebhook(
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature256,
            @RequestBody(required = false) JsonNode payload
    ) {
        facade.webhookEvent(event, deliveryId, signature256, payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/repositories/{repositoryId}/branches")
    public ResponseEntity<RepositoryBranchesResponse> getBranches(
            @PathVariable Long repositoryId,
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        RepositoryBranchesResponse response =
                facade.getBranches(userDetail.getUser(), repositoryId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/repositories")
    public ResponseEntity<List<RepositoryResponse>> getRepositories(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        List<RepositoryResponse> res =
                facade.getRepositories(userDetail.getUser());
        return ResponseEntity.ok(res);
    }
}
