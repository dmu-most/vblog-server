package com.example.vblogserver.domain.user.dto;

import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.entity.SocialType;
import com.example.vblogserver.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class UserInfoDto {
	private Long id;
	private String loginId;
	private String username;
	private String imageUrl;
	private SocialType socialType;

	public UserInfoDto(User user) {
		this.id = user.getId();
		this.loginId = user.getLoginId();
		this.username = user.getUsername();
		this.imageUrl = user.getImageUrl();
		this.socialType = user.getSocialType();
	}
}