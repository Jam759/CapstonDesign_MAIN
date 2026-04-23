package com.Hoseo.CapstoneDesign.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 멤버 초대 요청")
public class ProjectInviteRequest {
    @Schema(description = "초대할 프로젝트 ID", example = "201")
    private Long projectId;

    @Schema(description = "초대할 친구 사용자 ID 목록", example = "[3001, 3002]")
    private List<Long> friendIds;
}
