package com.Hoseo.CapstoneDesign.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 초대 요청")
public class ProjectInviteRequest {
    @Schema(description = "초대할 프로젝트 ID", example = "201")
    private Long projectId;

    @Schema(description = "초대 대상 사용자 ID", example = "3001")
    private Long inviteMemberId;
}
