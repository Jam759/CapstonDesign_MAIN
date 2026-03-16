package com.Hoseo.CapstoneDesign.support.fixture.auth;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserDetailSecurityContextFactory.class)
public @interface WithMockUserDetail {
    String serviceNickname() default "service-user";
    String oauthProviderId() default "github-provider-id";
    String oauthNickname() default "github-user";
}
