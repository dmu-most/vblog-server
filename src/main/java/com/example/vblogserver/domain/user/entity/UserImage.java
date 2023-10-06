package com.example.vblogserver.domain.user.entity;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserImage {
	private MultipartFile imageFile;

	public UserImage(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}
}
