package com.Hoseo.CapstoneDesign.project.dto.response;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자가 받은 프로젝트 초대 상태")
public class InviteStatusResponse {
    @Schema(description = "프로젝트 ID", example = "201")
    private Long projectId;

    @Schema(description = "초대 상태", example = "INVITED")
    private ProjectInviteStatus status;
}
