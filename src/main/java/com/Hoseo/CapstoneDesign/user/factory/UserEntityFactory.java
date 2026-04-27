package com.Hoseo.CapstoneDesign.user.factory;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.global.util.UuidUtil;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.UserTechStack;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;

import java.util.List;

public class UserEntityFactory {

    public static Users toUsers(OauthType oauthType, String oauthProviderId, String oauthNickname) {
        return Users.builder()
                .oauthNickname(oauthNickname)
                .systemRole(SystemRole.USER)
                .oauthType(oauthType)
                .identityId(UuidUtil.getUuidv7())
                .oauthProviderId(oauthProviderId)
                .profileComplete(false)
                .build();
    }

    public static UserInfoUpdateHistory toUserInfoUpdateHistory(
            Users user,
            String previousNickname,
            String previousOauthNickname
    ) {
        return UserInfoUpdateHistory.builder()
                .user(user)
                .previousNickname(previousNickname)
                .newNickname(user.getServiceNickname())
                .previousOauthNickname(previousOauthNickname)
                .newOauthNickname(user.getOauthNickname())
                .updatedAt(TimeUtil.getNowSeoulLocalDateTime())
                .updatedBy(user.getSystemRole())
                .build();
    }

    public static List<UserTechStack> toUserTechStackList(Users user, List<CommonGroupDetail> techStacks) {
        return techStacks.stream()
                .map(techStack -> UserTechStack.builder()
                        .user(user)
                        .userTechStack(techStack)
                        .build())
                .toList();
    }
}
