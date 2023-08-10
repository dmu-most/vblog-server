package com.example.vblogserver.global.config.auth;

import com.example.vblogserver.domain.user.entity.RefreshToken;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.RefreshTokenRepository;
import com.example.vblogserver.domain.user.service.UserService;
import com.example.vblogserver.global.config.jwt.TokenProvider;
import com.example.vblogserver.global.config.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REDIRECT_PATH = "/vblog";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION  = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res
            , Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        //User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));
        User user = userService.findByEmail(getEmail(authentication));


        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(req, res, refreshToken);

        // 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        // 인증 관련 설정 값, 쿠키 제거
        clearAuthenticationAttributes(req, res);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(req, res, targetUrl);
    }

    // 생성된 리프레시 토큰을 전달받아 데이터베이스에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest req
            ,HttpServletResponse res, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(req, res, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(res, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 인증 관련 설정 값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest req, HttpServletResponse res) {
        super.clearAuthenticationAttributes(req);
        // OAuth 인증을 위해 저장된 정보도 삭제
        authorizationRequestRepository.removeAuthorizationRequestCookies(req, res);
    }

    // 액세스 토큰을 패스에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    // email 반환
    private String getEmail(Authentication authentication) {
        OAuth2AuthenticationToken OAuthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String provider = OAuthToken.getAuthorizedClientRegistrationId();
        if(provider.equals("google")) {
            return (String) oAuth2User.getAttributes().get("email");
        } else if(provider.equals("kakao")) {
            Map<String, Object> account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            return (String) account.get("email");
        } else {
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            return (String) response.get("email");
        }
    }
}