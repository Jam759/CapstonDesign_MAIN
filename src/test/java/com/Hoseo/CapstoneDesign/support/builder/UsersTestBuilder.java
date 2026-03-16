package com.Hoseo.CapstoneDesign.support.builder;

import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;

import java.util.UUID;

public class UsersTestBuilder {

    private Long userId = null;
    private UUID identityId = UUID.randomUUID();
    private String serviceNickname = "service-user";
    private SystemRole systemRole = SystemRole.USER;
    private OauthType oauthType = OauthType.GITHUB;
    private String oauthProviderId = "github-provider-id";
    private String oauthNickname = "github-user";

    public static UsersTestBuilder defaultUser() {
        return new UsersTestBuilder();
    }

    public UsersTestBuilder userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public UsersTestBuilder identityId(UUID identityId) {
        this.identityId = identityId;
        return this;
    }

    public UsersTestBuilder serviceNickname(String serviceNickname) {
        this.serviceNickname = serviceNickname;
        return this;
    }

    public UsersTestBuilder systemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
        return this;
    }

    public UsersTestBuilder oauthType(OauthType oauthType) {
        this.oauthType = oauthType;
        return this;
    }

    public UsersTestBuilder oauthProviderId(String oauthProviderId) {
        this.oauthProviderId = oauthProviderId;
        return this;
    }

    public UsersTestBuilder oauthNickname(String oauthNickname) {
        this.oauthNickname = oauthNickname;
        return this;
    }

    public Users build() {
        return Users.builder()
                .userId(userId)
                .identityId(identityId)
                .serviceNickname(serviceNickname)
                .systemRole(systemRole)
                .oauthType(oauthType)
                .oauthProviderId(oauthProviderId)
                .oauthNickname(oauthNickname)
                .build();
    }
}
