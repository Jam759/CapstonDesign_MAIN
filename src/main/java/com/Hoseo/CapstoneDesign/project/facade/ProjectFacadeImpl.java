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
import com.Hoseo.CapstoneDesign.project.cache.service.ProjectResponseCacheService;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.service.ProjectQueryService;
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
import com.Hoseo.CapstoneDesign.project.event.ProjectMembershipChangedEvent;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.factory.ProjectDtoFactory;
import com.Hoseo.CapstoneDesign.project.factory.ProjectEntityFactory;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.project.service.ProjectTechStackService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Facade
@RequiredArgsConstructor
public class ProjectFacadeImpl implements ProjectFacade {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final ProjectTechStackService projectTechStackService;
    private final ProjectQueryService projectQueryService;
    private final CommonGroupDetailService commonGroupDetailService;
    private final GitHubAppInstallationService gitHubAppInstallationService;
    private final InstallationRepositoryService installationRepositoryService;
    private final AnalysisJobService analysisJobService;
    private final UserService userService;
    private final ProjectResponseCacheService projectResponseCacheService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = false)
    public ProjectCreateResponse createProject(ProjectCreateRequest request, AuthenticatedUserCacheEntry user) {
        Users owner = userService.getReferenceById(user.userId());
        Projects pjEntity = ProjectEntityFactory.toProjects(request, owner);
        GithubAppInstallations githubAppInstallations
                = gitHubAppInstallationService.getByUser(owner);
        InstallationRepository repository
                = installationRepositoryService.getByInstallationAndRepositoryId(
                githubAppInstallations,
                request.installationRepositoryId()
        );
        pjEntity.setTrackedSetting(githubAppInstallations, repository, request.trackedBranch());
        Projects savedPj = projectService.create(pjEntity);
        List<CommonGroupDetail> techStacks = request.stacks() == null
                ? List.of()
                : commonGroupDetailService.getRequiredReferencesByGroupAndIds(
                        CommonGroupDetailService.PROJECT_TECH_STACK_GROUP_ID,
                        request.stacks()
                );
        List<ProjectTechStack> pjTechStacks
                = ProjectEntityFactory.toProjectTechStackList(savedPj, techStacks);
        projectTechStackService.createAll(pjTechStacks);
        CommonGroupDetail ownerPosition = request.role() == null || request.role().isBlank()
                ? null
                : commonGroupDetailService.getRequiredReferenceByGroupAndId(
                        CommonGroupDetailService.PROJECT_POSITION_GROUP_ID,
                        request.role()
                );

        ProjectMember projectOwner =
                ProjectEntityFactory.toProjectsMember(
                        owner, savedPj,
                        ProjectMemberRole.OWNER,
                        ProjectInviteStatus.ACCEPTED,
                        ownerPosition
                );
        projectMemberService.create(projectOwner);

        List<ProjectMember> invitedMembers = List.of();
        if(!request.teamMemberIds().isEmpty()){
            invitedMembers = toInvitedMembers(savedPj, owner, request.teamMemberIds());
            projectMemberService.createAll(invitedMembers);
        }

        String idempotency = savedPj.getProjectId() + "-" + user.userId() + "-" + repository.getInstallationRepositoryId();
        AnalysisJob analysisJob = AnalysisJobEntityFactory.toAnalysisJob(
                savedPj,
                owner,
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
        publishProjectMembershipChanged(savedPj.getProjectId(), List.of(user.userId()));
        return ProjectDtoFactory.toProjectCreateResponse(savedPj, invitedMemberIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectThumbnailResponse> getMyProject(AuthenticatedUserCacheEntry user) {
        if (user == null || user.userId() == null) {
            return List.of();
        }

        return projectResponseCacheService.findMyProjects(user.userId())
                .orElseGet(() -> loadMyProject(user.userId()));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectSettingResponse getProjectSetting(Long projectId, AuthenticatedUserCacheEntry user) {
        Projects p = projectService.getById(projectId);
        if (!projectMemberService.isOwnerMember(projectId, user.userId()))
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        return ProjectDtoFactory.toProjectSettingResponse(p);
    }

    @Override
    @Transactional(readOnly = false)
    public ProjectSettingResponse updateProject(Long projectId, AuthenticatedUserCacheEntry user, ProjectSettingRequest request) {
        Users owner = userService.getReferenceById(user.userId());
        Projects p = projectService.getById(projectId);
        if (!projectMemberService.isOwnerMember(projectId, user.userId()))
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        if (p.getProjectStatus() != ProjectStatus.REPO_NOT_CONNECTED)
            throw new ProjectsException(ProjectsErrorCode.PROJECT_ALREADY_SETTING);
        GithubAppInstallations githubAppInstallations
                = gitHubAppInstallationService.getByUser(owner);
        InstallationRepository repository
                = installationRepositoryService.getByInstallationAndRepositoryId(
                githubAppInstallations,
                request.installationRepositoryId()
        );

        p.setTrackedSetting(githubAppInstallations, repository, request.trackedBranch());
        Projects savedProject = projectService.create(p);
        // full 분석 발행
        String idempotency = projectId + "-" + user.userId() + "-" + repository.getInstallationRepositoryId();
        AnalysisJob analysisJob = AnalysisJobEntityFactory.toAnalysisJob(
                savedProject,
                owner,
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

        publishProjectMembershipChanged(savedProject.getProjectId(), getProjectMemberUserIds(savedProject));
        return ProjectDtoFactory.toProjectSettingResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteProject(Long projectId, AuthenticatedUserCacheEntry user) {
        Projects project = projectService.getById(projectId);
        validateOwner(projectId, user);
        List<Long> affectedUserIds = getProjectMemberUserIds(project);

        projectService.delete(project);
        publishProjectMembershipChanged(project.getProjectId(), affectedUserIds);
    }

    @Override
    @Transactional(readOnly = false)
    public List<ProjectInviteResponse> inviteProject(ProjectInviteRequest request, AuthenticatedUserCacheEntry user) {
        Users owner = userService.getReferenceById(user.userId());
        Projects project = projectService.getById(request.getProjectId());
        validateOwner(project.getProjectId(), user);

        List<Long> friendIds = request.getFriendIds();
        if (friendIds == null || friendIds.isEmpty()) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_INVITE_INVALID_REQUEST);
        }

        Set<Long> existingMemberIds = new HashSet<>(
                projectMemberService.getUserIdsByProjectId(project.getProjectId())
        );

        List<ProjectInviteResponse> results = new ArrayList<>();
        for (Long invitedUserId : friendIds) {
            if (invitedUserId == null || invitedUserId.equals(user.userId())) {
                continue;
            }
            if (existingMemberIds.contains(invitedUserId)) {
                continue;
            }
            Users invitedUser = userService.getReferenceById(invitedUserId);
            ProjectMember saved = projectMemberService.create(
                    ProjectEntityFactory.toInvitedMember(invitedUser, project, owner)
            );
            results.add(toInviteResponse(saved));
        }
        return results;
    }

    @Override
    @Transactional(readOnly = false)
    public ProjectInviteResponse responseInvite(ProjectInviteResponseRequest request, AuthenticatedUserCacheEntry user) {
        ProjectMember invite = projectMemberService.getByIdAndUserId(request.getInviteId(), user.userId());
        ProjectInviteStatus responseStatus = Boolean.TRUE.equals(request.getAccepted())
                ? ProjectInviteStatus.ACCEPTED
                : ProjectInviteStatus.DECLINED;
        invite.respond(responseStatus);
        if (responseStatus == ProjectInviteStatus.ACCEPTED) {
            publishProjectMembershipChanged(invite.getProject().getProjectId(), List.of(user.userId()));
        }
        return toInviteResponse(invite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InviteStatusResponse> getMyInvitedList(AuthenticatedUserCacheEntry user) {
        if (user == null || user.userId() == null) {
            return List.of();
        }

        return projectQueryService.findMyInvites(user.userId());
    }

    private List<ProjectThumbnailResponse> loadMyProject(Long userId) {
        List<ProjectThumbnailResponse> response = projectQueryService.findMyProjectThumbnails(userId);
        projectResponseCacheService.saveMyProjects(userId, response);
        return response;
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

    private void validateOwner(Long projectId, AuthenticatedUserCacheEntry user) {
        if (user == null || user.userId() == null
                || !projectMemberService.isOwnerMember(projectId, user.userId())) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        }
    }

    private List<Long> getProjectMemberUserIds(Projects project) {
        return projectMemberService.getUserIdsByProjectId(project.getProjectId());
    }

    private void publishProjectMembershipChanged(Long projectId, List<Long> userIds) {
        applicationEventPublisher.publishEvent(new ProjectMembershipChangedEvent(projectId, userIds));
    }

    private ProjectInviteResponse toInviteResponse(ProjectMember member) {
        return new ProjectInviteResponse(
                member.getProjectMemberId(),
                member.getUser().getUserId(),
                member.getResponse()
        );
    }
}
