package com.Hoseo.CapstoneDesign.security.config;

public final class SecurityUrlPaths {

    public static final String AUTH_LOGOUT = "/api/v1/auth/logout";
    public static final String AUTH_REISSUE = "/api/v1/auth/reissue";
    public static final String GIT_HUB_WEBHOOK = "/api/v1/github/webhook/callback";
    public static final String GIT_HUB_WEBHOOK_SETUP = "/api/v1/github/setup/callback";
    public static final String ACTUATOR_HEALTH = "/actuator/health";
    public static final String ACTUATOR_PROMETHEUS = "/actuator/prometheus";
    // SSE 구독 엔드포인트. async dispatch 시 SecurityContext 가 비워지면서
    // AuthorizationFilter 가 Access Denied 를 내뱉는 문제가 있어 인가 자체는 permitAll.
    // 쿼리 파라미터 token 은 JwtAuthenticationFilter 에서 읽어 인증 주체를 주입함.
    public static final String NOTIFICATION_SSE_SUBSCRIBE = "/api/v1/notification/sse/subscribe";
    public static final String[] PERMIT_ALL_PATTERNS = {
            AUTH_REISSUE,
            GIT_HUB_WEBHOOK,
            GIT_HUB_WEBHOOK_SETUP,
            ACTUATOR_HEALTH,
            ACTUATOR_PROMETHEUS,
            NOTIFICATION_SSE_SUBSCRIBE,
            "/",
            "/favicon.ico",
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
            ACTUATOR_HEALTH,
            ACTUATOR_PROMETHEUS,
            "/swagger-ui",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**"
    };

    private SecurityUrlPaths() {
    }
}
