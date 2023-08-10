package com.example.vblogserver.global.config.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Getter
public class JwtFactory {

    private String subject = "test@test.com";
    private Date issuerAt = new Date();
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = Collections.emptyMap();

    // 빌더 패턴을 사용해 설정이 필요한 데이터만 선택 설정
    @Builder
    public JwtFactory(String subject, Date issuerAt, Date expiration,
                      Map<String, Object> claims) {
        this.subject = subject != null ? subject : this.subject;
        this.issuerAt = issuerAt != null ? issuerAt : this.issuerAt;
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;
    }

    public static JwtFactory withDefaultValue() {
        return JwtFactory.builder().build();
    }

    // jjwt 라이브러리를 사용해 JWT 토큰 생성
    public String createToken(JwtProperties jwtProperties) {
        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issuerAt)
                .setExpiration(expiration)
                .addClaims(claims)
                .signWith(jwtProperties.getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
