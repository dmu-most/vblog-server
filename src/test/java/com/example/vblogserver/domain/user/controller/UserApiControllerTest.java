package com.example.vblogserver.domain.user.controller;

import com.example.vblogserver.domain.user.dto.CreateAccessTokenRequest;
import com.example.vblogserver.domain.user.entity.RefreshToken;
import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.RefreshTokenRepository;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.domain.user.service.UserService;
import com.example.vblogserver.global.config.jwt.JwtFactory;
import com.example.vblogserver.global.config.jwt.JwtProperties;
import com.example.vblogserver.global.config.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext context;


    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @DisplayName("refreshToken으로 UserInfo를 가져올 수 있다.")
    @Test
    public void refreshTokenWithUserInfo() throws Exception {

        // given
        String url = "/api/userInfo";

        Optional<User> findUser = userRepository.findByEmail("test1234@naver.com");

        if(!findUser.isEmpty()) {
            userRepository.deleteById(findUser.get().getId());
        }

        User user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .nickname("testtest")
                .provider("naver")
                .providerId("1234")
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
        String token = objectMapper.writeValueAsString(request);

        ResultActions resultActions = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(token));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }
}
