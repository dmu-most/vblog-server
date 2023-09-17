package com.example.vblogserver.global.oauth2;

import com.example.vblogserver.domain.user.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * DefaultOAuth2User를 상속하고, email과 role 필드를 추가로 가진다.
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String loginId;
    private Role role;
    private String imageUrl;  // 이미지 URL을 저장하기 위한 필드
    private String username;  // 사용자 이름을 저장하기 위한 필드

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes, String nameAttributeKey,
        String loginId, Role role, String imageUrl, String username) {
        super(authorities, attributes, nameAttributeKey);
        this.loginId = loginId;
        this.role = role;
        this.imageUrl = imageUrl;  // 이미지 URL 설정
        this.username = username;  // 사용자 이름 설정
    }
}