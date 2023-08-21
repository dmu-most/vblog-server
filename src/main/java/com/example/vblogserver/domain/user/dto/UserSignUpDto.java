package com.example.vblogserver.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 자체 로그인 회원 가입 API에 RequestBody로 사용
@NoArgsConstructor
@Getter
public class UserSignUpDto {
	private String email; // 이메일
	private String loginid; // 아이디
	private String password; // 비밀번호
	private String username; // 이름
}