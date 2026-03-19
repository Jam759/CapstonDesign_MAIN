package com.Hoseo.CapstoneDesign.security.filter;

import com.Hoseo.CapstoneDesign.github.util.GitHubWebhookUtil;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.security.config.SecurityUrlPaths;
import com.Hoseo.CapstoneDesign.security.wrapper.CachedBodyHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class GithubWebhookSecurityFilter extends OncePerRequestFilter {

    private final GitHubWebhookUtil signatureVerifier;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(SecurityUrlPaths.GIT_HUB_WEBHOOK.equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        String signature256 = wrappedRequest.getHeader("X-Hub-Signature-256");
        String rawBody = wrappedRequest.getCachedBodyAsString();

        try {
            signatureVerifier.verify(signature256, rawBody);
            filterChain.doFilter(wrappedRequest, response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    new GlobalExceptionResponse(
                            9991, "잘못된 webhook", HttpStatus.UNAUTHORIZED
                    ).toString());
        }
    }
}