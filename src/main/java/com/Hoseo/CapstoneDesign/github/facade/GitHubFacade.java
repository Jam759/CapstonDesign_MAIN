package com.Hoseo.CapstoneDesign.github.facade;

import com.Hoseo.CapstoneDesign.github.dto.response.InstallationsAvailableResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryBranchesResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryResponse;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.util.List;

public interface GitHubFacade {

    InstallationsAvailableResponse getAvailable(AuthenticatedUserCacheEntry user, String returnTo);

    URI connectInstallationIdAndUser(String state, Long installationId, String setupAction);

    void webhookEvent(String event, String deliveryId, String signature256, JsonNode payload);

    RepositoryBranchesResponse getBranches(AuthenticatedUserCacheEntry user, Long repositoryId);

    List<RepositoryResponse> getRepositories(AuthenticatedUserCacheEntry user);
}
