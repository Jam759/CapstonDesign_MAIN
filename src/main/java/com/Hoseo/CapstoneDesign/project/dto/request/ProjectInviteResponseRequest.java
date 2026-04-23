package com.Hoseo.CapstoneDesign.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 초대 응답 요청")
public class ProjectInviteResponseRequest {

    @Schema(description = "초대 ID (ProjectMember PK)", example = "23101")
    private Long inviteId;

    @Schema(description = "수락 여부 (true=수락, false=거절)", example = "true")
    private Boolean accepted;
}
