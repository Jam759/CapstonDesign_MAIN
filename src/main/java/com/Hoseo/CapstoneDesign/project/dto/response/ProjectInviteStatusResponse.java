package com.Hoseo.CapstoneDesign.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자가 받은 프로젝트 초대 정보")
public class ProjectInviteStatusResponse {
    @Schema(description = "초대 ID (ProjectMember PK)", example = "23101")
    private Long id;

    @Schema(description = "초대한 사용자 닉네임", example = "alice")
    private String from;

    @Schema(description = "프로젝트 이름", example = "캡스톤 디자인")
    private String projectName;

    @Schema(description = "초대 상태 (INVITED / ACCEPTED / DECLINED)", example = "INVITED")
    private String status;
}
