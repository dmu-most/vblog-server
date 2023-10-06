package com.example.vblogserver.domain.user.service;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
	private static final String USER_IMAGE_FOLDER = "userImage/";
	private final AmazonS3 s3Client;
	private final UserRepository userRepository;
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String uploadFile(MultipartFile multipartFile) throws IOException {
		String fileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(multipartFile.getSize());

		s3Client.putObject(
			bucket,
			USER_IMAGE_FOLDER + fileName,
			multipartFile.getInputStream(),
			objectMetadata
		);

		return s3Client.getUrl(bucket, USER_IMAGE_FOLDER + fileName).toString();
	}

	public String updateProfileImage(User user, MultipartFile file) throws IOException {
		if (user.getImageUrl() != null) {
			deleteProfileImage(user);
		}

		String imageUrl = uploadFile(file);
		user.setImageUrl(imageUrl);

		userRepository.save(user);

		return imageUrl;
	}

	public void deleteProfileImage(User user) {
		if (user.getImageUrl() != null) {
			URI url = URI.create(user.getImageUrl());
			String key = url.getPath().substring(1); // URL에서 버킷 이름 다음부터가 key 입니다.

			s3Client.deleteObject(bucket, key);
			user.setImageUrl(null);
			userRepository.save(user);
		}
	}
}