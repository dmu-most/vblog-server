package com.example.vblogserver.global.jwt.controller;

import java.util.Optional;

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
@RequiredArgsConstructor
public class TokenController {

	private final JwtService jwtService;

	@PostMapping("/verify/access")
	public ResponseEntity<ResponseDto> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
		// 액세스 토큰과 리프레시 토큰 모두 추출
		Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
		Optional<String> refreshTokenOpt = jwtService.extractRefreshToken(request);

		if (accessTokenOpt.isPresent() && refreshTokenOpt.isPresent()) { // 두 종류의 토큰이 모두 제공된 경우
			String accessToken = accessTokenOpt.get();
			String refreshToken = refreshTokenOpt.get();

			if (!jwtService.isTokenValid(refreshToken)) { // 리프레시 토큰이 유효하지 않은 경우
				throw new InvalidTokenException("만료된 리프레시 토큰입니다.");
			}

			if (jwtService.isTokenExpired(accessToken)) { // 액세스 토큰이 만료된 경우

				String loginId = jwtService.extractId(refreshToken)
					.orElseThrow(() -> new InvalidTokenException("만료된 액세스 토큰입니다."));

				String newAccessToken = jwtService.createAccessToken(loginId);

				jwtService.sendAccessToken(response, newAccessToken);

				return ResponseEntity.ok().body(
						new ResponseDto(true, "새로운 액세스 토큰이 발급되었습니다.")
				);
			} else { // 아직 유효한 액세스 토큰

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
					new ResponseDto(false, "액세스 토큰이 아직 유효합니다.")
				);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				new ResponseDto(false, "액세스 토큰 또는 리프레시 토큰이 헤더에 제공되지 않았습니다.")
			);
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