package com.Hoseo.CapstoneDesign.analysis.dto.application;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.PhaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Project roadmap phase")
public class ProjectPhaseDto {
    @Schema(description = "Phase id", example = "1")
    private Long id;

    @Schema(description = "Phase order", example = "1")
    private Integer phaseOrder;

    @Schema(description = "Phase name", example = "MVP Build")
    private String phaseName;

    @Schema(description = "Phase objective", example = "Build an end-to-end MVP")
    private String phaseObjective;

    @Schema(description = "Expected phase outcome", example = "Users can complete the main project flow")
    private String phaseOutcome;

    @Schema(description = "Phase scope list", example = "[\"auth\", \"project-create\", \"analysis\"]")
    private List<String> phaseScope;

    @Schema(description = "Phase exit criteria", example = "Main APIs and screens pass integration testing")
    private String exitCriteria;

    @Schema(description = "Milestone ids that belong to this phase", example = "[10, 11]")
    private List<Long> milestoneIds;

    @Schema(description = "Phase status. Serialized for FE as not_started, in_progress, completed", example = "in_progress")
    private PhaseStatus status;
}
