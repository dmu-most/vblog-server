package com.example.vblogserver.global.config;

import static org.springframework.security.config.Customizer.*;

import java.util.Arrays;

import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.global.oauth2.handler.OAuth2LoginFailureHandler;
import com.example.vblogserver.global.oauth2.handler.OAuth2LoginSuccessHandler;
import com.example.vblogserver.global.oauth2.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.example.vblogserver.global.login.handler.LoginFailureHandler;
import com.example.vblogserver.global.login.handler.LoginSuccessHandler;
import com.example.vblogserver.global.login.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	private final LoginService loginService;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;


	@Bean
	public WebSecurityCustomizer configure() { // 스프링 시큐리티 기능 비활성화
		return (web) -> web.ignoring()
			.requestMatchers("/img/**", "/css/**", "/js/**", "/favicon.ico", "/h2-console/**");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		System.out.println("filterChain 진입");

		http.cors(withDefaults()) // CORS 설정 추가
			.csrf((csrf) -> csrf.disable())
			.httpBasic((httpBasic) -> httpBasic.disable())
			.formLogin((formLogin) -> formLogin.disable());
			//.logout((logout) -> logout.disable());

		http.sessionManagement((sessionManagement)
			-> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요
		http.authorizeRequests((authReq)
			-> authReq.requestMatchers( "/","/login","/signup", "/logout", "/check-email", "/check-id", "/login/**", "/token/**",
						"/oauth2/code/**","/img/**", "/css/**", "/js/**", "/favicon.ico", "/vblog-api.html", "/swagger-ui/**", "/api-docs/**").permitAll()
				.requestMatchers("/api/**").hasRole(Role.USER.name())
			.anyRequest().permitAll()); // 위의 경로 이외에는 모두 인증된 사용자만 접근 가능

		//== 소셜 로그인 설정 ==//
		http.oauth2Login((login) -> login.loginPage("/login")
				.successHandler(oAuth2LoginSuccessHandler) // 인증 성공 시 핸들러, 동의하고 계속하기를 눌렀을 때 Handler 설정
				.failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
				.userInfoEndpoint((endPoint) -> endPoint.userService(customOAuth2UserService)) // customUserService 설정
		);

		//http.logout((logout) -> logout.logoutSuccessUrl("/login"));

		// 헤더를 확인할 커스텀 필터 추가
		http
			.addFilterAfter(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(customJsonUsernamePasswordAuthenticationFilter(), JwtAuthenticationProcessingFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * AuthenticationManager 설정 후 등록
	 * PasswordEncoder를 사용하는 AuthenticationProvider 지정 (PasswordEncoder는 위에서 등록한 PasswordEncoder 사용)
	 * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
	 * UserDetailsService는 커스텀 LoginService로 등록
	 * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
	 *
	 */
	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(loginService);
		return new ProviderManager(provider);
	}

	/**
	 * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
	 */
	@Bean
	public LoginSuccessHandler loginSuccessHandler() {
		return new LoginSuccessHandler(jwtService, userRepository);
	}

	/**
	 * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
	 */
	@Bean
	public LoginFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}

	/**
	 * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
	 * 커스텀 필터를 사용하기 위해 만든 커스텀 필터를 Bean으로 등록
	 * setAuthenticationManager(authenticationManager())로 위에서 등록한 AuthenticationManager(ProviderManager) 설정
	 * 로그인 성공 시 호출할 handler, 실패 시 호출할 handler로 위에서 등록한 handler 설정
	 */
	@Bean
	public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
		CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
			= new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
		customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
		customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
		return customJsonUsernamePasswordLoginFilter;
	}

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService, userRepository);
		return jwtAuthenticationFilter;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(
			Arrays.asList("http://localhost:3000", "http://dmu-vblog.s3-website.ap-northeast-2.amazonaws.com"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Refresh", "Cache-Control", "Content-Type"));

		/* 응답 헤더 설정 추가*/
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Refresh"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}