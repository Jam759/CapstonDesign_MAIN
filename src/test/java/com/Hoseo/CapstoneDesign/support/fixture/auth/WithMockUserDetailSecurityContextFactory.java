package com.Hoseo.CapstoneDesign.support.fixture.auth;

import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockUserDetailSecurityContextFactory implements WithSecurityContextFactory<WithMockUserDetail> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserDetail annotation) {
        Users user = UsersTestBuilder.defaultUser()
                .serviceNickname(annotation.serviceNickname())
                .oauthProviderId(annotation.oauthProviderId())
                .oauthNickname(annotation.oauthNickname())
                .build();

        UserDetailImpl principal = new UserDetailImpl(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
