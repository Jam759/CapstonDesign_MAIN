package com.Hoseo.CapstoneDesign.project.service;

import com.Hoseo.CapstoneDesign.project.dto.query.InviteStatusQueryRow;
import com.Hoseo.CapstoneDesign.project.dto.query.ProjectThumbnailQueryRow;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.project.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectQueryService {

    private final ProjectMapper mapper;

    public List<ProjectThumbnailResponse> findMyProjectThumbnails(Long userId) {
        return mapper.findMyProjectThumbnails(userId).stream()
                .map(this::toThumbnailResponse)
                .toList();
    }

    public List<ProjectInviteStatusResponse> findMyInvites(Long userId) {
        return mapper.findMyInvites(userId).stream()
                .map(this::toInviteStatusResponse)
                .toList();
    }

    private ProjectThumbnailResponse toThumbnailResponse(ProjectThumbnailQueryRow row) {
        List<String> techStacks = splitCsv(row.techStacksCsv());
        List<String> teamMembers = splitCsv(row.teamMembersCsv());
        return ProjectThumbnailResponse.builder()
                .projectId(row.projectId())
                .id(row.projectId())
                .title(row.title())
                .name(row.title())
                .type(teamMembers.size() > 1 ? "team" : "personal")
                .role(row.role())
                .description(row.description())
                .startDate(row.startDate())
                .endDate(row.endDate())
                .techStack(techStacks)
                .stacks(techStacks)
                .githubRepo(row.repositoryFullName())
                .githubBranch(row.trackedBranch())
                .teamMembers(teamMembers)
                .build();
    }

    private ProjectInviteStatusResponse toInviteStatusResponse(InviteStatusQueryRow row) {
        return ProjectInviteStatusResponse.builder()
                .id(row.id())
                .from(row.from() != null ? row.from() : "")
                .projectName(row.projectName())
                .status(row.status())
                .build();
    }

    private List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.asList(csv.split(","));
    }
}
