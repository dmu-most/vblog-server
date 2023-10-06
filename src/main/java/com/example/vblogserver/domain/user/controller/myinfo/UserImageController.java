package com.example.vblogserver.domain.user.controller.myinfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.vblogserver.domain.user.dto.UserInfoDto;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.domain.user.service.ImageService;
import com.example.vblogserver.domain.user.service.UserService;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserImageController {
	private final UserService userService;
	private final ImageService imageService;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	@GetMapping("/myinfo/users/image")
	public ResponseEntity<UserInfoDto> getUserProfileImage(HttpServletRequest request) throws Exception {
		String accessToken = jwtService.extractAccessToken(request)
			.orElseThrow(() -> new Exception("액세스 토큰이 없습니다."));

		User user = userService.getUserByAccessToken(accessToken);

		return ResponseEntity.ok(new UserInfoDto(user));
	}

	@PostMapping("/myinfo/users/image")
	public ResponseEntity<UserInfoDto> uploadProfileImage(HttpServletRequest request, @RequestPart MultipartFile file) throws Exception {
		String accessToken = jwtService.extractAccessToken(request)
			.orElseThrow(() -> new Exception("액세스 토큰이 없습니다."));

		User user = userService.getUserByAccessToken(accessToken);

		String newImageUrl = imageService.uploadFile(file);
		user.setImageUrl(newImageUrl);
		userRepository.save(user);

		user = userService.getUserByAccessToken(accessToken);

		return ResponseEntity.ok(new UserInfoDto(user));
	}

	@PatchMapping("/myinfo/users/image")
	public ResponseEntity<UserInfoDto> updateProfileImage(HttpServletRequest request, @RequestPart MultipartFile file) throws Exception {
		String accessToken = jwtService.extractAccessToken(request)
			.orElseThrow(() -> new Exception("액세스 토큰이 없습니다."));

		User user = userService.getUserByAccessToken(accessToken);

		String newImageUrl = imageService.updateProfileImage(user, file);
		user.setImageUrl(newImageUrl);
		userRepository.save(user);

		// 사용자 정보 갱신
		user = userService.getUserByAccessToken(accessToken);

		return ResponseEntity.ok(new UserInfoDto(user));
	}

	@DeleteMapping("/myinfo/users/image")
	public ResponseEntity<UserInfoDto> deleteProfileImage(HttpServletRequest request) throws Exception {
		String accessToken = jwtService.extractAccessToken(request)
			.orElseThrow(() -> new Exception("액세스 토큰이 없습니다."));

		User user = userService.getUserByAccessToken(accessToken);
		imageService.deleteProfileImage(user);

		return ResponseEntity.ok(new UserInfoDto(user));
	}
}


