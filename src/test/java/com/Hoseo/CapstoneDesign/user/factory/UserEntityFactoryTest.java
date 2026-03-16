package com.Hoseo.CapstoneDesign.user.factory;

import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserEntityFactoryTest.class);

    @Test
    @DisplayName("toUsers 는 기본 ROLE(USER)과 identityId를 채운다")
    void toUsersBuildsDefaultFields() {
        Users user = UserEntityFactory.toUsers(OauthType.GITHUB, "provider-x", "oauth-x");

        assertThat(user.getOauthType()).isEqualTo(OauthType.GITHUB);
        assertThat(user.getOauthProviderId()).isEqualTo("provider-x");
        assertThat(user.getOauthNickname()).isEqualTo("oauth-x");
        assertThat(user.getSystemRole()).isEqualTo(SystemRole.USER);
        assertThat(user.getIdentityId()).isNotNull();
        log.info("[TEST] factory toUsers contract validated");
    }

    @Test
    @DisplayName("toUserInfoUpdateHistory 는 이전/신규 닉네임 스냅샷을 구성한다")
    void toUserInfoUpdateHistoryBuildsSnapshot() {
        Users user = UsersTestBuilder.defaultUser()
                .serviceNickname("after-nick")
                .oauthNickname("after-oauth")
                .systemRole(SystemRole.USER)
                .build();

        UserInfoUpdateHistory history = UserEntityFactory.toUserInfoUpdateHistory(
                user,
                "before-nick",
                "before-oauth"
        );

        assertThat(history.getUser()).isEqualTo(user);
        assertThat(history.getPreviousNickname()).isEqualTo("before-nick");
        assertThat(history.getNewNickname()).isEqualTo("after-nick");
        assertThat(history.getPreviousOauthNickname()).isEqualTo("before-oauth");
        assertThat(history.getNewOauthNickname()).isEqualTo("after-oauth");
        assertThat(history.getUpdatedBy()).isEqualTo(SystemRole.USER);
        assertThat(history.getUpdatedAt()).isNotNull();
        log.info("[TEST] factory history snapshot validated");
    }
}
