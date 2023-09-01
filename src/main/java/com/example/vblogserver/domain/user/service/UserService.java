package com.example.vblogserver.domain.user.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;

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

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        if (userRepository.findByLoginid(userSignUpDto.getLoginid()).isPresent()) {
            throw new Exception("이미 존재하는 아이디입니다.");
        }

        if (userRepository.findByUsername(userSignUpDto.getUsername()).isPresent()) {
            throw new Exception("이미 존재하는 별명입니다.");
        }

        User user = User.builder()
            .email(userSignUpDto.getEmail())
            .password(userSignUpDto.getPassword())
            .loginid(userSignUpDto.getLoginid())
            .username(userSignUpDto.getUsername())
            .role(Role.USER)
            .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }

    public boolean isLoginIdDuplicated(String loginid) {
        return userRepository.findByLoginid(loginid).isPresent();
    }

    public boolean isEmailDuplicated(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameDuplicated(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User login(String loginid, String password) throws Exception {
        User user = userRepository.findByLoginid(loginid)
            .orElseThrow(() -> new Exception("아이디 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        return user;
    }

}
