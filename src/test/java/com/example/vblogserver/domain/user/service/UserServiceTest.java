package com.example.vblogserver.domain.user.service;

import com.example.vblogserver.domain.user.dto.CreateAccessTokenRequest;
import com.example.vblogserver.domain.user.dto.UserInfoDto;
import com.example.vblogserver.domain.user.entity.RefreshToken;
import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.RefreshTokenRepository;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.config.jwt.JwtFactory;
import com.example.vblogserver.global.config.jwt.JwtProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;


@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;


    @DisplayName("RefreshToken으로 UserInfo 조회")
    @Test
    public void getUserInfo() throws Exception {

        //given
        Optional<User> findUser = userRepository.findByEmail("test45@naver.com");

        if(!findUser.isEmpty()) {
            userRepository.deleteById(findUser.get().getId());
        }

        User user = userRepository.save(User.builder()
                .email("test45@naver.com")
                .nickname("testtest")
                .provider("naver")
                .providerId("3223")
                .role(Role.USER)
                .build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", user.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);


        // when
        Optional<User> findRefreshUser = userRepository.findByRefreshTokenWithUser(refreshToken);

        // then
        Assertions.assertThat(user.getId()).isEqualTo(findRefreshUser.get().getId());
    }


    @DisplayName("User의 nickname을 update한다.")
    @Test
    public void updateNickname() throws Exception {

        // given
        Long userId = 19L; // DB에 저장되어 있는 userId

        Optional<User> oldUser = userRepository.findById(userId);
        String oldNick = oldUser.get().getNickname();
        String changeNickname = oldNick + "12";
        if(changeNickname.length() > 15) changeNickname = "123";

        UserInfoDto oldUserInfo = new UserInfoDto(oldUser.get().getId(), oldUser.get().getEmail(), changeNickname);


        // when
        UserInfoDto updateUser = userService.updateNickname(oldUserInfo);
        String newNick = updateUser.getNickname();

        // then
        Assertions.assertThat(oldNick).isNotEqualTo(newNick);
    }
}
