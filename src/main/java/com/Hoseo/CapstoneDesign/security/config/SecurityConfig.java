package com.Hoseo.CapstoneDesign.security.config;

import com.Hoseo.CapstoneDesign.github.util.GitHubWebhookUtil;
import com.Hoseo.CapstoneDesign.security.filter.GithubWebhookSecurityFilter;
import com.Hoseo.CapstoneDesign.security.filter.GlobalExceptionFilter;
import com.Hoseo.CapstoneDesign.security.filter.JwtAuthenticationFilter;
import com.Hoseo.CapstoneDesign.security.filter.TraceMdcFilter;
import com.Hoseo.CapstoneDesign.security.handler.CustomAccessDeniedHandler;
import com.Hoseo.CapstoneDesign.security.handler.CustomAuthenticationEntryPoint;
import com.Hoseo.CapstoneDesign.security.handler.GithubOAuth2SuccessHandler;
import com.Hoseo.CapstoneDesign.security.service.AccessTokenBlackListService;
import com.Hoseo.CapstoneDesign.security.service.impl.UserDetailServiceImpl;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import com.Hoseo.CapstoneDesign.global.logging.StructuredHttpLogger;
import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailServiceImpl userService;
    private final AccessTokenBlackListService blackListService;

    private final GitHubWebhookUtil gitHubWebhookUtil;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final GithubOAuth2SuccessHandler githubOAuth2SuccessHandler;

    @Bean
    public GlobalExceptionFilter globalExceptionFilter(StructuredHttpLogger structuredHttpLogger) {
        return new GlobalExceptionFilter(structuredHttpLogger);
    }

    @Bean
    public TraceMdcFilter traceMdcFilter(LoggingProperties loggingProperties) {
        return new TraceMdcFilter(loggingProperties);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userService, blackListService);
    }

    @Bean
    public GithubWebhookSecurityFilter githubWebhookSecurityFilter() {
        return new GithubWebhookSecurityFilter(gitHubWebhookUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            TraceMdcFilter traceMdcFilter,
            GlobalExceptionFilter globalExceptionFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
                //기본적인 설정
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                //세션 사용 안 함
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //예외 처리 등록 filter내에서 못잡는 예외 던지면 인마들이 잡음
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityUrlPaths.AUTH_LOGOUT).authenticated()
                        .requestMatchers(SecurityUrlPaths.PERMIT_ALL_PATTERNS).permitAll()
                        .requestMatchers("/admin/**").hasRole(SystemRole.ADMIN.name())
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> {
                    oauth.successHandler(githubOAuth2SuccessHandler);
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(globalExceptionFilter, JwtAuthenticationFilter.class)
                .addFilterBefore(traceMdcFilter, GlobalExceptionFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowCredentials(true);
        c.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500"
        ));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        c.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }


    @Bean
    public FilterRegistrationBean<GithubWebhookSecurityFilter> githubWebhookFilterRegistration() {
        FilterRegistrationBean<GithubWebhookSecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(githubWebhookSecurityFilter());
        registration.addUrlPatterns(SecurityUrlPaths.GIT_HUB_WEBHOOK);
        registration.setOrder(1);
        return registration;
    }

}

