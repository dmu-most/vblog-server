package com.example.vblogserver.domain.user.dto;

import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String nickname;
    private String email;
    private String provider;
    private String providerId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String nickname, String email, String provider, String providerId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.nickname = nickname;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;

    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }

        else if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }

        else {
            return ofKaKao(userNameAttributeName, attributes);
        }
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
            .email((String) attributes.get("email"))
            .providerId((String) attributes.get(userNameAttributeName))
            .provider("google")
            .attributes(attributes)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
            .email((String) response.get("email"))
            .provider("naver")
            .providerId((String) response.get(userNameAttributeName))
            .attributes(response)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }

    private static OAuthAttributes ofKaKao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

        return OAuthAttributes.builder()
            .email((String) account.get("email"))
            .providerId(attributes.get(userNameAttributeName).toString())
            .provider("kakao")
            .attributes(account)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }

    public User toEntity() {
        return User.builder()
            .nickname(getNickname())
            .email(email)
            .role(Role.USER)
            .providerId(providerId)
            .provider(provider)
            .build();
    }

    private String getNickname() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        uuid = uuid.substring(0, 10);
        return uuid;
    }
}
