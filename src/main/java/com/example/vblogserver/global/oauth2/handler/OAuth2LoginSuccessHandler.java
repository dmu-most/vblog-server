package com.example.vblogserver.global.oauth2.handler;

import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.oauth2.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            Map<String, String> tokens = loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성

            String redirectUrlWithTokensAndUserInfo =
                "http://dmu-vblog.s3-website.ap-northeast-2.amazonaws.com/oauth/callback?" +
                    "Authorization=Bearer " + tokens.get("accessToken") +
                    "&RefreshToken=Bearer " + tokens.get("refreshToken") +
                    "&imageUrl=" + URLEncoder.encode(oAuth2User.getImageUrl(), StandardCharsets.UTF_8.name()) +
                    "&username=" + URLEncoder.encode(oAuth2User.getUsername(), StandardCharsets.UTF_8.name());

            response.sendRedirect(redirectUrlWithTokensAndUserInfo);
        } catch (Exception e) {
            throw e;
        }
    }

    private Map<String,String> loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String loginId = oAuth2User.getLoginId(); // 로그인 ID 추출
        String accessToken = jwtService.createAccessToken(oAuth2User.getLoginId());
        String refreshToken = jwtService.createRefreshToken(loginId);

        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        userRepository.findByLoginId(loginId)
            .ifPresent(user -> {
                user.updateRefreshToken(refreshToken);
                userRepository.saveAndFlush(user);
            });

        Map<String,String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }
}