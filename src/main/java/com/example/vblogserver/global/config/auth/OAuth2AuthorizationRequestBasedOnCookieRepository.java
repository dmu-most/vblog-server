package com.example.vblogserver.global.config.auth;

import com.example.vblogserver.global.config.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

/**
 * OAuth2에 필요한 정보를 쿠키에 저장해서 쓸 수 있도록 인증 요청과 관련된 상태를 저장
 */
public class OAuth2AuthorizationRequestBasedOnCookieRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS = 18000;

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest req, HttpServletResponse res) {
        return this.loadAuthorizationRequest(req);
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest req) {
        Cookie cookie = WebUtils.getCookie(req, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authReq, HttpServletRequest req, HttpServletResponse res) {
        if(authReq == null) {
            removeAuthorizationRequest(req, res);
            return;
        }

        CookieUtil.addCookie(res, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authReq), COOKIE_EXPIRE_SECONDS);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest req, HttpServletResponse res) {
        CookieUtil.deleteCookie(req, res, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}
