package com.example.vblogserver.domain.user.service;

import com.example.vblogserver.domain.user.dto.UserInfoDto;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.config.error.CustomException;
import com.example.vblogserver.global.config.error.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }


    /**
     * Refresh Token으로 UserInfo 조회
     *
     * @param refreshToken
     * @return
     */
    public UserInfoDto getUserInfo(String refreshToken) {
        Optional<User> findUser = userRepository.findByRefreshTokenWithUser(refreshToken);

        if (findUser.isEmpty()) {
            return null;
        }

        UserInfoDto userInfo = new UserInfoDto(findUser.get().getId(), findUser.get().getEmail(), findUser.get().getNickname());

        return userInfo;
    }

    /**
     * 사용자 닉네임 수정
     * @param nickname
     * @return UserInfoDto
     */
    @Transactional
    public UserInfoDto updateNickname(String nickname, String token) {

        Long userId = getCurrentUserId(token); // 토큰 값으로 userId 가져오기

        Optional<User> findUser = userRepository.findById(userId);

        String newNickname = nickname;
        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if(newNickname.length() > 15 || newNickname.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_NICKNAME);
        }

        if (newNickname.equals(findUser.get().getNickname())) {
            return new UserInfoDto(findUser.get().getId(), findUser.get().getEmail(), findUser.get().getNickname());
        }

        // 닉네임 중복체크
        boolean flag = dupCheckNickname(newNickname);

        if(flag) {
            throw new CustomException(ErrorCode.HAS_NICKNAME);
        }

        findUser.get().updateNickname(newNickname);

        UserInfoDto updateUserInfo = new UserInfoDto(findUser.get().getId(),
            findUser.get().getEmail(), findUser.get().getNickname());

        return updateUserInfo;
    }



    /**
     * 사용자 닉네임 중복체크
     * @param nickname
     * @return boolean
     */
    public boolean dupCheckNickname(String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);

        if (findUser.isEmpty()) return false;

        return true;
    }

    /**
     * 현재 로그인한 사용자 정보 가져오기
     * @return
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * 현재 로그인한 사용자 ID 반환
     * @return userId
     */
    public Long getCurrentUserId(String token) {
        return tokenService.getUserId(token);
    }
}