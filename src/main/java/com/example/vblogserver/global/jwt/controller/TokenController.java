package com.example.vblogserver.global.jwt.controller;

import java.util.Optional;

import com.example.vblogserver.global.jwt.util.TokenExpiredException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.user.dto.ResponseDto;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.jwt.util.InvalidTokenException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/token")
public class TokenController {

	private final JwtService jwtService;

	public TokenController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@PostMapping("/verify/access")
	public ResponseEntity<ResponseDto> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
		// 액세스 토큰 추출
		Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

		if (accessTokenOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
					new ResponseDto(false, "액세스 토큰이 제공되지 않았습니다.")
			);
		}

		String accessToken = accessTokenOpt.get();

		try {
			if (!jwtService.isTokenValid(accessToken)) { // 액세스 토큰이 유효하지 않은 경우
				// 리프레시 토큰 추출
				Optional<String> refreshTokenOpt = jwtService.extractRefreshToken(request);

				if (refreshTokenOpt.isPresent()) { // 리프레시 토큰이 있는 경우
					String refreshToken = refreshTokenOpt.get();
					if (jwtService.isTokenValid(refreshToken)) { // 리프레시 토큰 유요성 검사

						String loginId = jwtService.extractId(refreshToken)
								.orElseThrow(() -> new InvalidTokenException("만료된 액세스 토큰입니다."));

						String newAccessToken = jwtService.createAccessToken(loginId);

						jwtService.sendAccessToken(response, newAccessToken);

						return ResponseEntity.ok().body(
								new ResponseDto(true, "새로운 액세스 토큰이 발급되었습니다.")
						);
					}
				}

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
						new ResponseDto(false, "유요하지 않은 액세스/리프레시  토근입니다.")
				);
			} else {
				return ResponseEntity.ok().body(
						new ResponseDto(true, "액세스트키가 유요합니다.")
				);
			}
		} catch (InvalidTokenException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} catch (TokenExpiredException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
	}

	@PostMapping("/reissue/refresh")
	public ResponseEntity<ResponseDto> refreshRefreshToken(HttpServletRequest request,
														   HttpServletResponse response) {

		String refreshToken = jwtService.extractRefreshToken(request)
				.filter(jwtService::isTokenValid)
				.orElseThrow(() -> new InvalidTokenException("만료된 리프레시 토큰입니다."));

		String loginId = jwtService.extractId(refreshToken)
				.orElseThrow(() -> new InvalidTokenException("만료된 리프레시 토큰입니다."));

		String newRefreshToken = jwtService.createRefreshToken(loginId);
		jwtService.sendAccessToken(response, newRefreshToken);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

		return ResponseEntity.ok().headers(responseHeaders).body(
				new ResponseDto(true, "새로운 리프레시 토큰이 발급되었습니다.")
		);
	}
}
