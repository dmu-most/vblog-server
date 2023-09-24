package com.example.vblogserver.domain.user.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.board.dto.BoardDTO;
import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.bookmark.dto.BookmarkDTO;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.entity.BookmarkFolder;
import com.example.vblogserver.domain.bookmark.repository.BookmarkFolderRepository;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.click.repository.ClickRepository;
import com.example.vblogserver.domain.review.controller.ReviewService;
import com.example.vblogserver.domain.review.dto.ReviewDTO;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.domain.user.service.UserService;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myinfo")
public class MypageController {
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ClickRepository clickRepository;
	private final BookmarkRepository bookmarkRepository;
	private final BookmarkFolderRepository bookmarkFolderRepository;
	private final ReviewRepository reviewRepository;
	private final BoardRepository boardRepository;

	// 리뷰 조회 (페이징 처리 : 10개)
	@GetMapping("/blog/reviews")
	public ResponseEntity<Page<ReviewDTO>> getUserBlogReviews(HttpServletRequest request,
		@RequestParam(defaultValue = "0") int page) {
		return getUserReviewsByCategory(request, "blog", PageRequest.of(page, 10));
	}

	@GetMapping("/vlog/reviews")
	public ResponseEntity<Page<ReviewDTO>> getUserVlogReviews(HttpServletRequest request,
		@RequestParam(defaultValue = "0") int page) {
		return getUserReviewsByCategory(request, "vlog", PageRequest.of(page, 10));
	}

	private ResponseEntity<Page<ReviewDTO>> getUserReviewsByCategory(HttpServletRequest request, String category, Pageable pageable) {		// 액세스 토큰 추출
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
		User user = userRepository.findByLoginId(loginIdOpt.get()).orElseThrow(() -> new RuntimeException("User not found"));

		Page<Review> reviews = reviewRepository.findReviewsByUserAndBoard_CategoryG_CategoryNameIgnoreCase(user, category, pageable);

		if (reviews.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<ReviewDTO> reviewDTOs = reviews.getContent().stream()
			.map(review -> {
				ReviewDTO reviewDTO = new ReviewDTO();
				reviewDTO.setReviewId(review.getId());
				reviewDTO.setContent(review.getContent());
				reviewDTO.setCreatedDate(review.getCreatedDate());
				reviewDTO.setBoardId(review.getBoard().getId());  // 게시글 ID 설정

				// Category 정보 설정
				if (review.getBoard().getCategoryG() != null) {
					reviewDTO.setCategory(review.getBoard().getCategoryG().getCategoryName());
				}

				reviewDTO.setGrade(review.getGrade());

				return reviewDTO;
			})
			.collect(Collectors.toList());

		PageImpl<ReviewDTO> resultPage = new PageImpl<>(reviewDTOs, pageable, reviews.getTotalElements());

		return ResponseEntity.ok(resultPage);
	}

	// 최근 기록
	@GetMapping("/blog/recently")
	public ResponseEntity<Page<BoardDTO>> getRecentlyViewedBlogBoards(HttpServletRequest request,
		@RequestParam(defaultValue = "0") int page) {
		return getRecentlyViewedBoardsByCategory(request,"blog", PageRequest.of(page, 10));
	}

	@GetMapping("/vlog/recently")
	public ResponseEntity<Page<BoardDTO>> getRecentlyViewedVlogBoards(HttpServletRequest request,
		@RequestParam(defaultValue = "0") int page) {
		return getRecentlyViewedBoardsByCategory(request,"vlog", PageRequest.of(page, 10));
	}

	private ResponseEntity<Page<BoardDTO>> getRecentlyViewedBoardsByCategory(HttpServletRequest request, String category, Pageable pageable) {
		// 액세스 토큰 추출
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
		User user = userRepository.findByLoginId(loginIdOpt.get()).orElseThrow(() -> new RuntimeException("User not found"));

		Page<Click> clicks = clickRepository.findByUser(user, pageable);

		if (clicks.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<Long> boardIds = clicks.getContent().stream()
			.map(click -> click.getBoard().getId())
			.collect(Collectors.toList());

		Page<Board> boards = boardRepository.findByIdInAndCategoryG_CategoryNameIgnoreCase(boardIds,
			category,
			pageable);

		List<BoardDTO> boardDTOs = boards.getContent().stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());

		PageImpl<BoardDTO> resultPage = new PageImpl<>(boardDTOs, pageable, boards.getTotalElements());

		return ResponseEntity.ok(resultPage);
	}

	private BoardDTO convertToDto(Board board){
		return new BoardDTO(board);
	}
}
