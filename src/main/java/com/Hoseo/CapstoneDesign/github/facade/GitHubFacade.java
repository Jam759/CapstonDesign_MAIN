package com.Hoseo.CapstoneDesign.github.facade;

import com.Hoseo.CapstoneDesign.github.dto.response.InstallationsAvailableResponse;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;

public interface GitHubFacade {

    InstallationsAvailableResponse getAvailable(Users user, String returnTo);

    URI connectInstallationIdAndUser(String state, Long installationId, String setupAction);

    void webhookEvent(String event, String deliveryId, String signature256, JsonNode payload);
}
