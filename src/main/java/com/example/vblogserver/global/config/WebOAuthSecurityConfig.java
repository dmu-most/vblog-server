package com.example.vblogserver.global.config;

import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.repository.RefreshTokenRepository;
import com.example.vblogserver.domain.user.service.UserService;
import com.example.vblogserver.global.config.auth.OAuth2UserCustomService;
import com.example.vblogserver.global.config.auth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.vblogserver.global.config.auth.OAuth2SuccessHandler;
import com.example.vblogserver.global.config.jwt.TokenAuthenticationFilter;
import com.example.vblogserver.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure() { // 스프링 시큐리티 기능 비활성화
        return (web) -> web.ignoring()
                .requestMatchers("/img/**", "/css/**");
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("filterChain 진입");
        http.csrf((csrf) -> csrf.disable())
                .httpBasic((httpBasic) -> httpBasic.disable())
                .formLogin((formLogin) -> formLogin.disable())
                .logout((logout) -> logout.disable());

        http.sessionManagement((sessionManagement)
                -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 헤더를 확인할 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요
        http.authorizeHttpRequests((authReq)
                -> authReq.requestMatchers("/vblog","/api/token", "/login/**",
                        "/oauth2/authorization/**", "/js/**","/favicon.ico", "/vblog-api.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/vblog/**").authenticated()
                .anyRequest().permitAll());

        http.oauth2Login((login) -> login.loginPage("/login")
                .authorizationEndpoint((endPoint)
                        -> endPoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .successHandler(oAuth2SuccessHandler()) // 인증 성공 시 핸들러
                .userInfoEndpoint((endPoint) -> endPoint.userService(oAuth2UserCustomService))
        );


        http.logout((logout) -> logout.logoutSuccessUrl("/login"));

        // /auth로 시작하는 url인 경우 401 상태 코드 반환
        http.exceptionHandling((exception)
                -> exception.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                , new AntPathRequestMatcher("/login/**")));

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }


    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

}
