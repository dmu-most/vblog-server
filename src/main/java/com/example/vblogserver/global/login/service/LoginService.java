package com.example.vblogserver.global.login.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		User user = userRepository.findByLoginid(loginId)
			.orElseThrow(() -> new UsernameNotFoundException("해당 아이디가 존재하지 않습니다."));

		return org.springframework.security.core.userdetails.User.builder()
			.username(user.getLoginid())
			.password(user.getPassword())
			.roles(user.getRole().name())
			.build();
	}
}
