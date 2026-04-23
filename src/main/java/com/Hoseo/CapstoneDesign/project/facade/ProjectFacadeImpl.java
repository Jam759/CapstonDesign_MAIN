package com.Hoseo.CapstoneDesign.project.facade;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisJobEntityFactory;
import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisEventType;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteResponseRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.InviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectCreateResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.ProjectTechStack;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberRole;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectStatus;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.factory.ProjectDtoFactory;
import com.Hoseo.CapstoneDesign.project.factory.ProjectEntityFactory;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.project.service.ProjectTechStackService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Facade
@RequiredArgsConstructor
public class ProjectFacadeImpl implements ProjectFacade {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final ProjectTechStackService projectTechStackService;
    private final CommonGroupDetailService commonGroupDetailService;
    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;
    private final AnalysisJobService analysisJobService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = false)
    public ProjectCreateResponse createProject(ProjectCreateRequest request, Users user) {
        Projects pjEntity = ProjectEntityFactory.toProjects(request, user);
        GithubAppInstallations githubAppInstallations
                = gitHubAppInstallationService.getByUser(user);
        InstallationRepository repository
                = installationRepositoryService.getByInstallationAndRepositoryId(
                githubAppInstallations,
                request.installationRepositoryId()
        );
        pjEntity.setTrackedSetting(githubAppInstallations, repository, request.trackedBranch());
        Projects savedPj = projectService.create(pjEntity);
        List<CommonGroupDetail> techStacks = request.stacks() == null
                ? List.of()
                : commonGroupDetailService.getReferencesByIds(request.stacks());
        List<ProjectTechStack> pjTechStacks
                = ProjectEntityFactory.toProjectTechStackList(savedPj, techStacks);
        projectTechStackService.createAll(pjTechStacks);
        CommonGroupDetail ownerPosition = request.role() == null || request.role().isBlank()
                ? null
                : commonGroupDetailService.getReferenceById(request.role());

        ProjectMember projectOwner =
                ProjectEntityFactory.toProjectsMember(
                        user, savedPj,
                        ProjectMemberRole.OWNER,
                        ProjectInviteStatus.ACCEPTED,
                        ownerPosition
                );
        projectMemberService.create(projectOwner);

        List<ProjectMember> invitedMembers = List.of();
        if(!request.teamMemberIds().isEmpty()){
            invitedMembers = toInvitedMembers(savedPj, user, request.teamMemberIds());
            projectMemberService.createAll(invitedMembers);
        }

        String idempotency = savedPj.getProjectId() + "-" + user.getUserId() + "-" + repository.getInstallationRepositoryId();
        AnalysisJob analysisJob = AnalysisJobEntityFactory.toAnalysisJob(
                savedPj,
                user,
                githubAppInstallations,
                repository,
                null,
                null,
                request.trackedBranch(),
                idempotency,
                AnalysisEventType.FULL_SCAN_ANALYSIS_REQUEST,
                repository.isPrivate()
        );
        analysisJobService.createPendingJob(analysisJob);

        List<Long> invitedMemberIds = invitedMembers.stream()
                .map(ProjectMember::getUser)
                .map(Users::getUserId)
                .toList();
        return ProjectDtoFactory.toProjectCreateResponse(savedPj, invitedMemberIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectThumbnailResponse> getMyProject(Users user) {
        if (user == null || user.getUserId() == null) {
            return List.of();
        }

        return projectMemberService.getAcceptedMembersByUserId(user.getUserId())
                .stream()
                .map(this::toThumbnail)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectSettingResponse getProjectSetting(Long projectId, Users user) {
        Projects p = projectService.getById(projectId);
        if (!projectMemberService.isOwnerMember(projectId, user.getUserId()))
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        return ProjectDtoFactory.toProjectSettingResponse(p);
    }

    @Override
    @Transactional(readOnly = false)
    public ProjectSettingResponse updateProject(Long projectId, Users user, ProjectSettingRequest request) {
        Projects p = projectService.getById(projectId);
        if (!projectMemberService.isOwnerMember(projectId, user.getUserId()))
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        if (p.getProjectStatus() != ProjectStatus.REPO_NOT_CONNECTED)
            throw new ProjectsException(ProjectsErrorCode.PROJECT_ALREADY_SETTING);
        GithubAppInstallations githubAppInstallations
                = gitHubAppInstallationService.getByUser(user);
        InstallationRepository repository
                = installationRepositoryService.getByInstallationAndRepositoryId(
                githubAppInstallations,
                request.installationRepositoryId()
        );

        p.setTrackedSetting(githubAppInstallations, repository, request.trackedBranch());
        Projects savedProject = projectService.create(p);
        // full 분석 발행
        String idempotency = projectId + "-" + user.getUserId() + "-" + repository.getInstallationRepositoryId();
        AnalysisJob analysisJob = AnalysisJobEntityFactory.toAnalysisJob(
                savedProject,
                user,
                githubAppInstallations,
                repository,
                null,
                null,
                request.trackedBranch(),
                idempotency,
                AnalysisEventType.FULL_SCAN_ANALYSIS_REQUEST,
                repository.isPrivate()
        );
        analysisJobService.createPendingJob(analysisJob);

        return ProjectDtoFactory.toProjectSettingResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteProject(Long projectId, Users user) {
        Projects project = projectService.getById(projectId);
        validateOwner(projectId, user);

        projectService.delete(project);
    }

    @Override
    @Transactional(readOnly = false)
    public List<ProjectInviteResponse> inviteProject(ProjectInviteRequest request, Users user) {
        Projects project = projectService.getById(request.getProjectId());
        validateOwner(project.getProjectId(), user);

        List<Long> friendIds = request.getFriendIds();
        if (friendIds == null || friendIds.isEmpty()) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_INVITE_INVALID_REQUEST);
        }

        List<ProjectInviteResponse> results = new ArrayList<>();
        for (Long invitedUserId : friendIds) {
            if (invitedUserId == null || invitedUserId.equals(user.getUserId())) {
                continue;
            }
            if (projectMemberService.existsProjectMember(project.getProjectId(), invitedUserId)) {
                continue;
            }
            Users invitedUser = userService.getReferenceById(invitedUserId);
            ProjectMember saved = projectMemberService.create(
                    ProjectEntityFactory.toInvitedMember(invitedUser, project, user)
            );
            results.add(toInviteResponse(saved));
        }
        return results;
    }

    @Override
    @Transactional(readOnly = false)
    public ProjectInviteResponse responseInvite(ProjectInviteResponseRequest request, Users user) {
        ProjectMember invite = projectMemberService.getByIdAndUserId(request.getInviteId(), user.getUserId());
        ProjectInviteStatus responseStatus = Boolean.TRUE.equals(request.getAccepted())
                ? ProjectInviteStatus.ACCEPTED
                : ProjectInviteStatus.DECLINED;
        invite.respond(responseStatus);
        return toInviteResponse(invite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InviteStatusResponse> getMyInvitedList(Users user) {
        if (user == null || user.getUserId() == null) {
            return List.of();
        }

        return projectMemberService.getMemberInvitesByUserId(user.getUserId())
                .stream()
                .map(member -> InviteStatusResponse.builder()
                        .id(member.getProjectMemberId())
                        .from(member.getInvitedBy() != null ? displayName(member.getInvitedBy()) : "")
                        .projectName(member.getProject().getTitle())
                        .status(member.getResponse().name())
                        .build())
                .toList();
    }

    private List<ProjectMember> toInvitedMembers(Projects project, Users owner, List<Long> teamMemberIds) {
        if (teamMemberIds == null || teamMemberIds.isEmpty()) {
            return List.of();
        }

        List<ProjectMember> members = new ArrayList<>();
        teamMemberIds.stream()
                .filter(Objects::nonNull)
                .filter(userId -> !userId.equals(owner.getUserId()))
                .distinct()
                .forEach(userId -> {
                    Users invitedUser = userService.getReferenceById(userId);
                    members.add(ProjectEntityFactory.toInvitedMember(invitedUser, project, owner));
                });
        return members;
    }

    private ProjectThumbnailResponse toThumbnail(ProjectMember currentMember) {
        Projects project = currentMember.getProject();
        List<String> techStacks = projectTechStackService.getByProject(project)
                .stream()
                .map(ProjectTechStack::getProjectTechStack)
                .map(CommonGroupDetail::getCommonGroupDetailId)
                .toList();
        List<String> teamMembers = projectMemberService.getProjectMember(project)
                .stream()
                .map(ProjectMember::getUser)
                .map(this::displayName)
                .toList();
        return ProjectDtoFactory.toProjectThumbnailResponse(project, techStacks, teamMembers, memberRole(currentMember));
    }

    private String displayName(Users user) {
        if (user.getServiceNickname() != null && !user.getServiceNickname().isBlank()) {
            return user.getServiceNickname();
        }
        return user.getOauthNickname();
    }

    private String memberRole(ProjectMember member) {
        if (member.getProjectPositionCmd() != null) {
            return member.getProjectPositionCmd().getCommonGroupDetailId();
        }
        return member.getProjectRole().name().toLowerCase();
    }

    private void validateOwner(Long projectId, Users user) {
        if (user == null || user.getUserId() == null
                || !projectMemberService.isOwnerMember(projectId, user.getUserId())) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        }
    }

    private ProjectInviteResponse toInviteResponse(ProjectMember member) {
        return new ProjectInviteResponse(
                member.getProjectMemberId(),
                member.getUser().getUserId(),
                member.getResponse()
        );
    }
}
