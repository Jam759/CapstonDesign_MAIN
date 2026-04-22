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
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectCreateResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    @Transactional(readOnly = false)
    public ProjectCreateResponse createProject(ProjectCreateRequest request, Users user) {
        Projects pjEntity = ProjectEntityFactory.toProjects(request, user);
        Projects savedPj = projectService.create(pjEntity);
        List<CommonGroupDetail> techStacks = request.useTechStackCmdList() == null
                ? List.of()
                : commonGroupDetailService.getReferencesByIds(request.useTechStackCmdList());
        List<ProjectTechStack> pjTechStacks
                = ProjectEntityFactory.toProjectTechStackList(savedPj, techStacks);
        projectTechStackService.createAll(pjTechStacks);

        ProjectMember projectOwner =
                ProjectEntityFactory.toProjectsMember(
                        user, savedPj,
                        ProjectMemberRole.OWNER,
                        ProjectInviteStatus.ACCEPTED
                );
        projectMemberService.create(projectOwner);
        return ProjectDtoFactory.toProjectCreateResponse(savedPj);
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
}
