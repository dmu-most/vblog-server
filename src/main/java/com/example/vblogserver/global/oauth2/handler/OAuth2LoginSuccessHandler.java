package com.example.vblogserver.global.oauth2.handler;

import com.example.vblogserver.domain.user.entity.Role;
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
            CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
            loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성

            String redirectUrlWithTokensAndUserInfo =
                "http://dmu-vblog.s3-website.ap-northeast-2.amazonaws.com" +
                    "?Authorization=Bearer " + jwtService.createAccessToken(oAuth2User.getLoginId()) +
                    "&imageUrl=" + oAuth2User.getImageUrl() +
                    "&username=" + oAuth2User.getName();

            response.sendRedirect(redirectUrlWithTokensAndUserInfo);
        } catch (Exception e) {
            throw e;
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getLoginId());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        userRepository.findByLoginId(oAuth2User.getLoginId())
            .ifPresent(user -> {
                user.updateRefreshToken(refreshToken);
                userRepository.saveAndFlush(user);
            });
    }
}