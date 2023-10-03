package com.example.vblogserver.global.jwt.controller;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.user.dto.ResponseDto;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

	private final JwtService jwtService;

	/* 액세스 토큰의 만료 여부를 확인하고 그에 따라 적절한 응답을 반환
	 */
	@PostMapping("/verify/access")
	public ResponseEntity<ResponseDto> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
		// 액세스 토큰과 리프레시 토큰 모두 추출
		Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
		Optional<String> refreshTokenOpt = jwtService.extractRefreshToken(request);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

		if (accessTokenOpt.isPresent() && refreshTokenOpt.isPresent()) { // 두 종류의 토큰이 모두 제공된 경우
			String accessToken = accessTokenOpt.get();
			String refreshToken = refreshTokenOpt.get();

			if (!jwtService.isTokenValid(refreshToken)) { // 리프레시 토큰이 유효하지 않은 경우
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(responseHeaders).body(
					new ResponseDto(false, "유효하지 않은 리프레시 토큰입니다.")
				);
			}
			if (jwtService.isTokenExpired(accessToken)) { // 액세스 토큰이 만료된 경우

				// 리프레시 토큰에서 로그인 ID 추출 시도
				String loginId = jwtService.extractId(refreshToken)
					.orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

				// 새로운 액세스 토큰 생성 및 전송
				String newAccessToken = jwtService.createAccessToken(loginId);
				jwtService.sendAccessToken(response, newAccessToken);

				return ResponseEntity.ok().headers(responseHeaders).body(
					new ResponseDto(true, "새로운 액세스 토큰이 발급되었습니다.")
				);
			} else { // 아직 유효한 액세스 토큰

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(responseHeaders).body(
					new ResponseDto(false, "액세스 토큰이 아직 유효합니다.")
				);
			}
		} else { // 클라이언트가 토큰을 제공하지 않은 경우 (둘 중 하나라도 누락된 경우 400)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(responseHeaders).body(
				new ResponseDto(false, "액세스 토큰 또는 리프레시 토큰이 헤더에 제공되지 않았습니다. 다시 확인해주세요.")
			);
		}
	}

	/**
	 * 리프레시 토큰 재발급 로직
	 * 사용자가 명시적으로 로그아웃을 요청하거나 리프레시 토큰의 유효기간이 만료되었을 때
	 *
	 * @param request
	 * @param response
	 * @param responseHeaders
	 * @return
	 */
	@PostMapping("/reissu/refresh")
	public ResponseEntity<ResponseDto> refreshRefreshToken(HttpServletRequest request, HttpServletResponse response,
		HttpHeaders responseHeaders) {
		String refreshToken = jwtService.extractRefreshToken(request)
			.filter(jwtService::isTokenValid)
			.orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

		// 리프레시 토큰에서 로그인 ID 추출 시도
		String loginId = jwtService.extractId(refreshToken)
			.orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

		// 새로운 리프레시 토큰 생성 및 전송
		String newRefreshToken = jwtService.createRefreshToken(loginId);

		jwtService.sendAccessToken(response, newRefreshToken);

		return ResponseEntity.ok().headers(responseHeaders).body(
			new ResponseDto(true, "새로운 리프레시 토큰이 발급되었습니다.")
		);
	}
}
