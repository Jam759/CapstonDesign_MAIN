package com.Hoseo.CapstoneDesign.user.factory;

import com.Hoseo.CapstoneDesign.global.util.UuidUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;

public class UserEntityFactory {

    public static Users toUsers(OauthType oauthType, String oauthProviderId, String oauthNickname) {
        return Users.builder()
                .oauthNickname(oauthNickname)
                .systemRole(SystemRole.USER)
                .oauthType(oauthType)
                .identityId(UuidUtil.getUuidv7())
                .oauthProviderId(oauthProviderId)
                .build();
    }
}
