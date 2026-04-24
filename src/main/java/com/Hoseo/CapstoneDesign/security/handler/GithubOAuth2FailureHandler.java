package com.Hoseo.CapstoneDesign.security.handler;

import com.Hoseo.CapstoneDesign.security.properties.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubOAuth2FailureHandler implements AuthenticationFailureHandler {

    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        log.warn("[OAuth2] 인증 실패: {}", exception.getMessage());
        String redirectUrl = jwtProperties.frontRedirectUrl().toString() + "?error=oauth_failed";
        response.sendRedirect(redirectUrl);
    }
}
