package com.example.vblogserver.domain.user.service;

import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
	자체 로그인 회원 가입 시 사용하는 회원 가입 API 로직
	자체 로그인이기 때문에 클라이언트 요청에서 localId, userName 등 추가 정보까지 다 받아와서
	Builder로 추가 정보를 포함한 User Entity 생성 후 DB에 저장
	 */
    public void signUp(UserSignUpDto userSignUpDto) throws Exception {

        if (userRepository.findByLoginId(userSignUpDto.getLoginId()).isPresent()) {
            throw new Exception("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
            .password(userSignUpDto.getPassword())
            .loginId(userSignUpDto.getLoginId())
            .username(userSignUpDto.getUsername())
            .role(Role.USER)
            .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }

    public boolean isLoginIdDuplicated(String loginId) {
        // 아이디 길이 체크
        if (loginId.length() < 4 || loginId.length() > 12) {
            throw new IllegalArgumentException("아이디의 길이는 4~12자여야 합니다.");
        }

        // 알파벳 대소문자와 숫자로만 이루어져 있는지 체크
        if (!loginId.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("아이디는 알파벳 대소문자와 숫자로만 이루어져야 합니다.");
        }

        return userRepository.findByLoginId(loginId).isPresent();
    }

    public User login(String loginId, String password) throws Exception {
        User user = userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new Exception("아이디 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        return user;
    }

    @PostConstruct
    public void createTestUser() {
        User testUser = User.builder()
            .password("Test123!pw")
            .loginId("testuser")
            .username("testuser")
            .imageUrl("https://example.com/profile.jpg")
            .role(Role.USER)
            .socialId(null)
            .build();

        testUser.passwordEncode(passwordEncoder);
        userRepository.save(testUser);
    }

}
