package com.example.vblogserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.review.entity.Review;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String loginId; // 아이디
    private String password; // 비밀번호
    private String username; // 이름
    private String imageUrl; // 프로필 이미지
    @CreatedDate
    private LocalDateTime createDate; // 가입 날짜

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE
    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // 리프레시 토큰

    private String email;

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.USER;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    public User(String loginId, String password, String username) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.createDate = LocalDateTime.now();
    }

    // 내가 쓴 리뷰
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviews;

    // 스크랩
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Bookmark> bookmarks;
}