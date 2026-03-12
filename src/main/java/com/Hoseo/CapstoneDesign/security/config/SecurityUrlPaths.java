package com.Hoseo.CapstoneDesign.security.config;

public final class SecurityUrlPaths {

    public static final String AUTH_LOGOUT = "/api/v1/auth/logout";
    public static final String AUTH_REISSUE = "/api/v1/auth/reissue";
    public static final String[] PERMIT_ALL_PATTERNS = {
            AUTH_REISSUE,
            "/public/**",
            "/tmp/oauth2/test",
            "/tmp/oauth2/config",
            "/swagger-ui",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/oauth2/**",
            "/login/**"
    };

    public static final String[] JWT_FILTER_SKIP_PATTERNS = {
            "/swagger-ui",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**"
    };

    private SecurityUrlPaths() {
    }
}
