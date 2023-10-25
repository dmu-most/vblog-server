package com.example.vblogserver.global.login.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
										Authentication authentication) throws IOException {
		String loginId = extractUsername(authentication); // 인증 정보에서 Username(loginId) 추출
		String accessToken = jwtService.createAccessToken(loginId); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
		String refreshToken = jwtService.createRefreshToken(loginId); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

		userRepository.findByLoginId(loginId)
				.ifPresent(user -> {
					user.updateRefreshToken(refreshToken);
					userRepository.saveAndFlush(user);

					// 응답 헤더에 AccessToken, RefreshToken 실어서 응답
					response.setHeader("Authorization", accessToken);
					response.setHeader("Refresh", refreshToken);

					Map<String, Object> responseBody = new HashMap<>();

					responseBody.put("imageUrl", user.getImageUrl());
					responseBody.put("username", user.getUsername());

					responseBody.put("optionSelected", !user.getUserOptions().isEmpty());
					responseBody.put("options", user.getUserOptions());

					Gson gson = new GsonBuilder().serializeNulls().create();
					String jsonResponseBody = gson.toJson(responseBody);

					response.setContentType("application/json;charset=UTF-8");

					try {
						response.getWriter().write(jsonResponseBody);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});

		log.info("로그인에 성공하였습니다. Id: {}", loginId);
		log.info("로그인에 성공하였습니다. AccessToken: {}", accessToken);
		log.info("발급된 AccessToken 만료 기간: {}", accessTokenExpiration);
	}

	private String extractUsername(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return userDetails.getUsername();
	}
}