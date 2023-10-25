package com.example.vblogserver.domain.user.controller.myinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.dto.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.review.dto.ReviewDTO;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myinfo/reviews")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReviewsController {
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;

	// TODO: 최신순, 인기순
	@GetMapping("/blog")
	public ResponseEntity<PageResponseDto<ReviewDTO>> getUserBlogReviews(HttpServletRequest request, @RequestParam(defaultValue = "1") int page) {
		page = Math.max(page, 1); // 페이지가 1보다 작으면 1로 설정

		return getUserReviewsByCategory(request, "blog", PageRequest.of(page - 1, 5));
	}

	@GetMapping("/vlog")
	public ResponseEntity<PageResponseDto<ReviewDTO>> getUserVlogReviews(HttpServletRequest request, @RequestParam(defaultValue = "1") int page) {
		page = Math.max(page, 1); // 페이지가 1보다 작으면 1로 설정

		return getUserReviewsByCategory(request, "vlog", PageRequest.of(page - 1, 5));
	}

	private ResponseEntity<PageResponseDto<ReviewDTO>> getUserReviewsByCategory(HttpServletRequest request, String category,
		Pageable pageable) {        // 액세스 토큰 추출
		Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

		// 액세스 토큰이 존재하지 않거나 유효하지 않다면 에러 응답 반환
		if (accessTokenOpt.isEmpty() || !jwtService.isTokenValid(accessTokenOpt.get())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		// 액세스 토큰에서 로그인 아이디 추출
		Optional<String> loginIdOpt = jwtService.extractId(accessTokenOpt.get());

		// 로그인 아이디가 존재하지 않으면 에러 응답 반환
		if (loginIdOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		// 로그인 아이디로 사용자 조회
		User user = userRepository.findByLoginId(loginIdOpt.get())
			.orElseThrow(() -> new RuntimeException("User not found"));

		Page<Review> reviews = reviewRepository.findReviewsByUserAndBoard_CategoryG_CategoryNameIgnoreCase(user,
			category, pageable);

		List<ReviewDTO> reviewDTOs;

		if (reviews.isEmpty()) {
			reviewDTOs = new ArrayList<>();
		} else {
			reviewDTOs = reviews.getContent().stream()
					.map(review -> {
						ReviewDTO reviewDTO = new ReviewDTO();
						reviewDTO.setReviewId(review.getId());
						reviewDTO.setContent(review.getContent());
						reviewDTO.setCreatedDate(review.getCreatedDate());

						Board board = review.getBoard();

						if(board != null) {
							reviewDTO.setBoardId(board.getId());  // 게시글 ID 설정

							if (board.getCategoryG() != null) {
								reviewDTO.setCategory(board.getCategoryG().getCategoryName());
							}

							if (board.getTitle() != null) {  // 게시글 제목 설정 (추가)
								reviewDTO.setTitle(board.getTitle());
							}
						}

						return reviewDTO;
					})
					.collect(Collectors.toList());

		}

		PageResponseDto<ReviewDTO> responseDto =
				new PageResponseDto<>(reviewDTOs, reviews.getNumber(), reviews.getSize(), reviews.getTotalElements());

		return ResponseEntity.ok(responseDto);
	}
}
