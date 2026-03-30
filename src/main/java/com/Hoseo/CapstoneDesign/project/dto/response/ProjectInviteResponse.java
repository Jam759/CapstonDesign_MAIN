package com.Hoseo.CapstoneDesign.project.dto.response;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 초대 처리 결과")
public record ProjectInviteResponse(
        @Schema(description = "프로젝트 멤버 ID", example = "23101")
        Long projectMemberId,
        @Schema(description = "초대 대상 사용자 ID", example = "3001")
        Long invitedUserId,
        @Schema(description = "초대 상태", example = "INVITED")
        ProjectInviteStatus status
) {}
