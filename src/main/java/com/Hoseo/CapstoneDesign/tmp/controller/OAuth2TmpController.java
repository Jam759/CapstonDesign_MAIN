package com.Hoseo.CapstoneDesign.tmp.controller;

import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.Hoseo.CapstoneDesign.security.properties.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tmp/oauth2")
public class OAuth2TmpController {

    private final JwtProperties jwtProperties;

    @GetMapping("/test")
    public String testPage() {
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>OAuth2 GitHub 테스트</title>
                    <style>
                        :root {
                            --bg: #f5f7fb;
                            --panel: #ffffff;
                            --line: #dbe2f1;
                            --text: #1c2434;
                            --muted: #5f6b82;
                            --primary: #0e4bd4;
                        }
                        body {
                            margin: 0;
                            background: radial-gradient(circle at top, #ffffff 0%, var(--bg) 70%);
                            color: var(--text);
                            font-family: "Pretendard", "Noto Sans KR", sans-serif;
                        }
                        .wrap {
                            max-width: 980px;
                            margin: 36px auto;
                            padding: 0 18px 32px;
                        }
                        .card {
                            background: var(--panel);
                            border: 1px solid var(--line);
                            border-radius: 14px;
                            padding: 18px;
                            margin-bottom: 16px;
                        }
                        h1 { margin: 0 0 10px; font-size: 26px; }
                        h2 { margin: 0 0 10px; font-size: 18px; }
                        p { margin: 0 0 10px; color: var(--muted); }
                        .row { display: flex; gap: 10px; flex-wrap: wrap; }
                        button {
                            border: 0;
                            border-radius: 10px;
                            background: var(--primary);
                            color: #fff;
                            padding: 10px 14px;
                            font-size: 14px;
                            cursor: pointer;
                        }
                        button.alt { background: #38445d; }
                        pre {
                            margin: 0;
                            border: 1px solid var(--line);
                            border-radius: 10px;
                            background: #0f172a;
                            color: #dbeafe;
                            padding: 12px;
                            overflow: auto;
                            font-size: 12px;
                            line-height: 1.45;
                        }
                        code { background: #edf2ff; padding: 2px 6px; border-radius: 6px; }
                    </style>
                </head>
                <body>
                <div class="wrap">
                    <div class="card">
                        <h1>Spring Security OAuth2(GitHub) 점검 페이지</h1>
                        <p>1) GitHub 로그인 → 2) 리이슈 호출로 Access Token 발급 → 3) 인증 정보 확인 순서로 테스트하세요.</p>
                        <p>OAuth2 시작 URL: <code>/oauth2/authorization/github</code></p>
                        <div class="row">
                            <button id="btnLogin">GitHub 로그인 시작</button>
                            <button class="alt" id="btnConfig">서버 설정 확인</button>
                        </div>
                    </div>

                    <div class="card">
                        <h2>토큰/인증 테스트</h2>
                        <div class="row">
                            <button id="btnReissue">/api/v1/auth/reissue 호출</button>
                            <button class="alt" id="btnPrincipal">/tmp/oauth2/principal 호출</button>
                        </div>
                        <p id="tokenText">Access Token: (아직 없음)</p>
                    </div>

                    <div class="card">
                        <h2>응답</h2>
                        <pre id="out">페이지 로드됨</pre>
                    </div>
                </div>

                <script>
                    let accessToken = null;
                    const out = document.getElementById("out");
                    const tokenText = document.getElementById("tokenText");

                    function print(title, data) {
                        out.textContent = title + "\\n\\n" + JSON.stringify(data, null, 2);
                    }

                    async function postReissue() {
                        const res = await fetch("/api/v1/auth/reissue", {
                            method: "POST",
                            credentials: "include"
                        });
                        const text = await res.text();
                        let data;
                        try { data = JSON.parse(text); } catch (e) { data = { raw: text }; }
                        if (res.ok && data && data.accessToken) {
                            accessToken = data.accessToken;
                            tokenText.textContent = "Access Token: " + accessToken;
                        }
                        print("POST /api/v1/auth/reissue (" + res.status + ")", data);
                    }

                    async function getPrincipal() {
                        const headers = accessToken ? { "Authorization": "Bearer " + accessToken } : {};
                        const res = await fetch("/tmp/oauth2/principal", { headers });
                        const data = await res.json();
                        print("GET /tmp/oauth2/principal (" + res.status + ")", data);
                    }

                    async function getConfig() {
                        const res = await fetch("/tmp/oauth2/config");
                        const data = await res.json();
                        print("GET /tmp/oauth2/config (" + res.status + ")", data);
                    }

                    document.getElementById("btnLogin").addEventListener("click", () => {
                        location.href = "/oauth2/authorization/github";
                    });
                    document.getElementById("btnReissue").addEventListener("click", () => postReissue().catch(e => print("reissue error", { message: e.message })));
                    document.getElementById("btnPrincipal").addEventListener("click", () => getPrincipal().catch(e => print("principal error", { message: e.message })));
                    document.getElementById("btnConfig").addEventListener("click", () => getConfig().catch(e => print("config error", { message: e.message })));
                </script>
                </body>
                </html>
                """;
    }

    @GetMapping("/config")
    public Map<String, Object> config(HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("oauth2AuthorizationUrl", "/oauth2/authorization/github");
        result.put("oauth2CallbackTemplate", "{baseUrl}/login/oauth2/code/github");
        result.put("oauth2CallbackResolved", baseUrl + "/login/oauth2/code/github");
        result.put("frontRedirectUrl", jwtProperties.frontRedirectUrl().toString());
        result.put("reissueEndpoint", "/api/v1/auth/reissue");
        result.put("cookieName", jwtProperties.cookieName());
        result.put("cookiePathByService", "/api/v1/auth");
        result.put("requestUri", request.getRequestURI());
        return result;
    }

    @GetMapping("/principal")
    public Map<String, Object> principal(Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            result.put("authenticated", false);
            result.put("message", "인증 정보 없음. 먼저 reissue로 access token을 발급받아 Authorization 헤더로 호출하세요.");
            return result;
        }

        result.put("authenticated", authentication.isAuthenticated());
        result.put("name", authentication.getName());
        result.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Object principal = authentication.getPrincipal();
        result.put("principalType", principal == null ? null : principal.getClass().getName());

        if (principal instanceof UserDetailImpl userDetail) {
            result.put("userId", userDetail.getUser().getUserId());
            result.put("oauthType", userDetail.getUser().getOauthType());
            result.put("oauthNickname", userDetail.getUser().getOauthNickname());
        } else if (principal instanceof OAuth2User oauth2User) {
            result.put("oauth2Attributes", oauth2User.getAttributes());
        } else {
            result.put("principal", principal);
        }

        return result;
    }
}
