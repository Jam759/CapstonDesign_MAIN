package com.Hoseo.CapstoneDesign.security.filter;

import com.Hoseo.CapstoneDesign.security.exception.AccessTokenBlackListErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.AccessTokenBlackListException;
import com.Hoseo.CapstoneDesign.security.config.SecurityUrlPaths;
import com.Hoseo.CapstoneDesign.security.service.AccessTokenBlackListService;
import com.Hoseo.CapstoneDesign.security.service.impl.UserDetailServiceImpl;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtUtil jwtUtil;
    private final UserDetailServiceImpl userService;
    private final AccessTokenBlackListService blackListService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return Arrays.stream(SecurityUrlPaths.JWT_FILTER_SKIP_PATTERNS)
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtUtil.resolveTokenFromHttpServletRequest(request);
        //-> whiteList만들어서 permitAll엔 라우드 사인하는 식으로
        if (token != null) {

            // 토큰 검증
            jwtUtil.validateAccessToken(token);
            // Subject에서 UserId(UUID) 가져오기
            UUID userIdentificationId = jwtUtil.getSubjectFromAccessToken(token);
            UserDetails userDetails =
                    userService.loadUserByUsername(userIdentificationId.toString());

            //블랙리스트 확인
            if (blackListService.isExistByToken(token)) {
                AccessTokenBlackListErrorCode e = AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST;
                throw new AccessTokenBlackListException(e);
            }

            // Spring Security 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            token,
                            userDetails.getAuthorities() // UserDetails 기반이면 GrantedAuthority 제공
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        filterChain.doFilter(request, response);
    }

}
