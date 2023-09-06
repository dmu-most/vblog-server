package com.example.vblogserver.global.login.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Value("${jwt.access.expiration}")
	private String accessTokenExpiration;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		String loginid = extractUsername(authentication); // 인증 정보에서 Username(loginId) 추출
		String accessToken = jwtService.createAccessToken(loginid); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
		String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답

		userRepository.findByLoginid(loginid)
			.ifPresent(user -> {
				user.updateRefreshToken(refreshToken);
				userRepository. saveAndFlush(user);
			});
		log.info("로그인에 성공하였습니다. Id: {}", loginid);
		log.info("로그인에 성공하였습니다. AccessToken: {}", accessToken);
		log.info("발급된 AccessToken 만료 기간: {}", accessTokenExpiration);
	}

	private String extractUsername(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return userDetails.getUsername();
	}
}