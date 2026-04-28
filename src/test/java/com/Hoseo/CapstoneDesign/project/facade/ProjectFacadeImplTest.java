package com.Hoseo.CapstoneDesign.project.facade;

import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.github.service.InstallationRepositoryService;
import com.Hoseo.CapstoneDesign.project.cache.service.ProjectResponseCacheService;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberRole;
import com.Hoseo.CapstoneDesign.project.event.ProjectMembershipChangedEvent;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.security.cache.factory.AuthenticatedUserCacheFactory;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectQueryService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.project.service.ProjectTechStackService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectFacadeImplTest {

    private ProjectService projectService;
    private ProjectMemberService projectMemberService;
    private ProjectTechStackService projectTechStackService;
    private ProjectQueryService projectQueryService;
    private ProjectResponseCacheService projectResponseCacheService;
    private ApplicationEventPublisher applicationEventPublisher;
    private ProjectFacadeImpl facade;

    @BeforeEach
    void setUp() {
        projectService = mock(ProjectService.class);
        projectMemberService = mock(ProjectMemberService.class);
        projectTechStackService = mock(ProjectTechStackService.class);
        projectQueryService = mock(ProjectQueryService.class);
        projectResponseCacheService = mock(ProjectResponseCacheService.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        facade = new ProjectFacadeImpl(
                projectService,
                projectMemberService,
                projectTechStackService,
                projectQueryService,
                mock(CommonGroupDetailService.class),
                mock(GitHubAppInstallationService.class),
                mock(InstallationRepositoryService.class),
                mock(AnalysisJobService.class),
                mock(UserService.class),
                projectResponseCacheService,
                applicationEventPublisher
        );
    }

    @Test
    @DisplayName("returns cached my projects without loading project members")
    void getMyProjectReturnsCachedResponse() {
        Users user = Users.builder().userId(1L).build();
        List<ProjectThumbnailResponse> cached = List.of(ProjectThumbnailResponse.builder()
                .projectId(10L)
                .title("cached")
                .build());
        when(projectResponseCacheService.findMyProjects(1L)).thenReturn(Optional.of(cached));

        List<ProjectThumbnailResponse> result = facade.getMyProject(authenticated(user));

        assertThat(result).isEqualTo(cached);
        verify(projectMemberService, never()).getAcceptedMembersByUserId(1L);
    }

    @Test
    @DisplayName("loads my projects via query service and saves response cache on miss")
    void getMyProjectLoadsAndCachesOnMiss() {
        Users user = Users.builder().userId(1L).oauthNickname("me").build();
        List<ProjectThumbnailResponse> loaded = List.of(ProjectThumbnailResponse.builder()
                .projectId(10L)
                .title("project")
                .build());

        when(projectResponseCacheService.findMyProjects(1L)).thenReturn(Optional.empty());
        when(projectQueryService.findMyProjectThumbnails(1L)).thenReturn(loaded);

        List<ProjectThumbnailResponse> result = facade.getMyProject(authenticated(user));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getProjectId()).isEqualTo(10L);
        verify(projectResponseCacheService).saveMyProjects(1L, result);
    }

    @Test
    @DisplayName("publishes project membership changed event when project is deleted")
    void deleteProjectPublishesMembershipChangedEvent() {
        Users owner = Users.builder().userId(1L).build();
        Projects project = Projects.builder()
                .projectId(10L)
                .user(owner)
                .title("project")
                .build();

        when(projectService.getById(10L)).thenReturn(project);
        when(projectMemberService.isOwnerMember(10L, 1L)).thenReturn(true);
        when(projectMemberService.getUserIdsByProjectId(10L)).thenReturn(List.of(1L, 2L));

        facade.deleteProject(10L, authenticated(owner));

        ArgumentCaptor<ProjectMembershipChangedEvent> captor =
                ArgumentCaptor.forClass(ProjectMembershipChangedEvent.class);
        verify(applicationEventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().projectId()).isEqualTo(10L);
        assertThat(captor.getValue().userIds()).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("publishes project membership changed event when invite is accepted")
    void responseInvitePublishesMembershipChangedEventOnAccept() {
        Users user = Users.builder().userId(2L).build();
        Projects project = Projects.builder()
                .projectId(10L)
                .title("project")
                .build();
        ProjectMember invite = ProjectMember.builder()
                .projectMemberId(100L)
                .project(project)
                .user(user)
                .projectRole(ProjectMemberRole.MEMBER)
                .response(ProjectInviteStatus.INVITED)
                .build();

        when(projectMemberService.getByIdAndUserId(100L, 2L)).thenReturn(invite);

        facade.responseInvite(new com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteResponseRequest(100L, true), authenticated(user));

        ArgumentCaptor<ProjectMembershipChangedEvent> captor =
                ArgumentCaptor.forClass(ProjectMembershipChangedEvent.class);
        verify(applicationEventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().projectId()).isEqualTo(10L);
        assertThat(captor.getValue().userIds()).containsExactly(2L);
    }

    private AuthenticatedUserCacheEntry authenticated(Users user) {
        return AuthenticatedUserCacheFactory.fromUser(user);
    }
}
