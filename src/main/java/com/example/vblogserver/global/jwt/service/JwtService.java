package com.example.vblogserver.global.jwt.service;

import java.util.Date;
import java.util.Optional;

import com.example.vblogserver.global.jwt.util.TokenExpiredException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.util.InvalidTokenException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {
	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.access.expiration}")
	private Long accessTokenExpirationPeriod;

	@Value("${jwt.refresh.expiration}")
	private Long refreshTokenExpirationPeriod;

	@Value("${jwt.access.header}")
	private String accessHeader;

	@Value("${jwt.refresh.header}")
	private String refreshHeader;

	/**
	 * JWT의 Subject와 Claim으로 id 사용 -> 클레임의 name을 "id"으로 설정
	 * JWT의 헤더에 들어오는 값 : 'Authorization(Key) = Bearer {토큰} (Value)' 형식
	 */
	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	// private static final String EMAIL_CLAIM = "email";
	private static final String ID_CLAIM = "loginId";
	private static final String NAME_CLAIM = "username";
	private static final String BEARER = "Bearer ";

	private final UserRepository userRepository;

	/**
	 * AccessToken 생성 메소드
	 */
	public String createAccessToken(String loginId) {
		Date now = new Date();
		String compact = Jwts.builder() // JWT 토큰을 생성하는 빌더 반환
				.setSubject(ACCESS_TOKEN_SUBJECT)
				.setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
				.claim(ID_CLAIM, loginId)
				.signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
				.compact();
		return compact;
	}

	/**
	 * RefreshToken 생성
	 * RefreshToken은 Claim에 email도 넣지 않으므로 withClaim() X
	 */
	public String createRefreshToken(String loginId) {
		Date now = new Date();
		return Jwts.builder()
				.setSubject(REFRESH_TOKEN_SUBJECT)
				.setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
				.claim(ID_CLAIM, loginId)
				.signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
				.compact();
	}

	/**
	 * AccessToken 헤더에 실어서 보내기
	 */
	public void sendAccessToken(HttpServletResponse response, String accessToken) {
		response.setStatus(HttpServletResponse.SC_OK);

		response.setHeader(accessHeader, accessToken);
		log.info("재발급된 Access Token : {}", accessToken);
	}

	/**
	 * AccessToken + RefreshToken 헤더에 실어서 보내기
	 */
	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setStatus(HttpServletResponse.SC_OK);

		setAccessTokenHeader(response, accessToken);
		setRefreshTokenHeader(response, refreshToken);
		log.info("Access Token, Refresh Token 헤더 설정 완료");
	}


	// 클라이언트의 요청에서 JWT Token, ID를 추출하는 부분

	/**
	 * 헤더에서 RefreshToken 추출
	 * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
	 * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
	 */
	public Optional<String> extractRefreshToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(refreshHeader))
			.filter(refreshToken -> refreshToken.startsWith(BEARER))
			.map(refreshToken -> refreshToken.replace(BEARER, ""));
	}

	/**
	 * 헤더에서 AccessToken 추출
	 * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
	 * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
	 */
	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(accessHeader))
			.filter(refreshToken -> refreshToken.startsWith(BEARER))
			.map(refreshToken -> refreshToken.replace(BEARER, ""));
	}

	/**
	 * AccessToken에서 id 추출
	 * 추출 전에 JWT.require()로 검증기 생성
	 * verify로 AceessToken 검증 후
	 * 유효하다면 getClaim()으로 이메일 추출
	 * 유효하지 않다면 빈 Optional 객체 반환
	 */
	public Optional<String> extractId(String accessToken) {
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(secretKey.getBytes())
					.build()
					.parseClaimsJws(accessToken)
					.getBody();

			return Optional.ofNullable(claims.get(ID_CLAIM).toString());
		} catch (ExpiredJwtException e) {
			log.error("액세스 토큰이 만료되었습니다.", e);
		} catch (SignatureException e) {
			log.error("액세스 토큰의 서명 검증에 실패했습니다.", e);
		} catch (JwtException e) {
			log.error("액세스 토큰이 유효하지 않습니다.", e);
			throw new InvalidTokenException("유효하지 않은 액세스 토큰입니다.", e);
		}

		return Optional.empty();
	}

	/**
	 * AccessToken 헤더 설정
	 */
	public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(accessHeader, accessToken);
	}

	/**
	 * RefreshToken 헤더 설정
	 */
	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
		response.setHeader(refreshHeader, refreshToken);
	}

	public boolean isTokenValid(String token) {
		System.out.println("Token to validate: " + token);  // log the incoming token

		try {
			// JWT 파싱 및 검증 로직 수행
			Jws<Claims> claims = Jwts.parserBuilder()
					.setSigningKey(secretKey.getBytes())
					.build()
					.parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			throw new TokenExpiredException("만료된 토큰입니다.", e);
		} catch (JwtException e) {
			throw new InvalidTokenException("유효하지 않은 토큰입니다.", e);
		}

		// 만약 try-catch 블록을 통과하면, 토큰이 유효한 것으로 간주됨.
		return true;
	}

	/**
	 * 요청시 액세스 토큰의 만료 여부를 체크하고 만료되었을 경우에만 재발급
	 */
	public boolean isTokenExpired(String token) {
		System.out.println("Token to validate: " + token);  // log the incoming token
		try {
			Jws<Claims> claims = Jwts.parserBuilder()
					.setSigningKey(secretKey.getBytes())
					.build()
					.parseClaimsJws(token);

			Date expiration = claims.getBody().getExpiration();
			Date now = new Date();

			if (expiration.before(now)) {  // If the expiration date is before current time
				return true;  // The token is expired
			}

		} catch (Exception e) {
			throw new InvalidTokenException("유효하지 않은 토큰입니다.", e);
		}

		// 만약 try-catch 블록을 통과하면, 토큰이 유효한 것으로 간주됨.
		return false;
	}
}
