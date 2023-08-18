package com.example.vblogserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;
    //private String password;

    //private String username; // 이름
    @Column(unique = true, nullable = false)
    private String email; // 가입 이메일

    @Column(nullable = false, unique = true, length = 15)  // 닉네임 중복 허용 할건지?
    private String nickname; // 닉네임
    //private String profileUrl; // 프로필 사진
    @CreatedDate
    private LocalDateTime createDate; // 가입 날짜

    //OAuth2
    private String provider; // google, naver, kakao
    private String providerId; // OAuth의 key(id)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(String email, String nickname, String provider, String providerId, Role role) {
        this.email = email;
        this.nickname = nickname;
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

    //public void setPassword(String password) { this.password = password; }

}