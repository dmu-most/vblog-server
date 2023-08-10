package com.example.vblogserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    private String pw;

    @Column(nullable = false, unique = true)
    private String userId; // 유저 아이디
    private String name; // 이름
    private String nickname; // 별명
    @Column(unique = true, nullable = false)
    private String email; // 가입 이메일
    private String profileUrl; // 프로필 사진
    @CreatedDate
    private LocalDateTime createDate; // 가입 날짜

    //OAuth2
    private String provider; // google, naver, kakao
    private String providerId; // OAuth의 key(id)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(String email, String userId, String name, String nickname, String profileUrl, String provider, String providerId, Role role) {
        this.email = email;
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public User updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }
}