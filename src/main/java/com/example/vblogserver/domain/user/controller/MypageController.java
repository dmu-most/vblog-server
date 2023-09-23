package com.example.vblogserver.domain.user.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.board.dto.BoardDTO;
import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.bookmark.dto.BookmarkDTO;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
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
@RequestMapping("/mypage")
public class MypageController {
	private final UserService userService;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final ClickRepository clickRepository;

	@GetMapping("/blog/reviews")
	public ResponseEntity<List<ReviewDTO>> getUserBlogReviews(HttpServletRequest request) {
		return getUserReviewsByCategory(request, "blog");
	}

	@GetMapping("/vlog/reviews")
	public ResponseEntity<List<ReviewDTO>> getUserVlogReviews(HttpServletRequest request) {
		return getUserReviewsByCategory(request, "vlog");
	}

	private ResponseEntity<List<ReviewDTO>> getUserReviewsByCategory(HttpServletRequest request, String category) {
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

		List<Review> reviews = user.getReviews().stream()
			.filter(review -> review.getBoard().getCategoryG().getCategoryName().equalsIgnoreCase(category))
			.collect(Collectors.toList());

		if (reviews.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<ReviewDTO> reviewDTOs = reviews.stream()
			.map(review -> {
				ReviewDTO reviewDTO = new ReviewDTO();
				reviewDTO.setReviewId(review.getId());
				reviewDTO.setContent(review.getContent());
				reviewDTO.setCreatedDate(review.getCreatedDate());
				reviewDTO.setBoardId(review.getBoard().getId());  // 게시글 ID 설정

				// Category 정보 설정
				if (review.getBoard().getCategoryG() != null) {
					reviewDTO.setCategory(review.getBoard().getCategoryG().getCategoryName());  // CategoryG의 getName() 메소드는 해당 객체에서 구현되어야 함.
				}

				reviewDTO.setGrade(review.getGrade());

				return reviewDTO;
			})
			.collect(Collectors.toList());

		return ResponseEntity.ok(reviewDTOs);
	}

	@GetMapping("/blog/scraps")
	public ResponseEntity<List<BookmarkDTO>> getUserBlogBookmarks(HttpServletRequest request) {
		return getUserBookmarksByCategory(request, "blog");
	}

	@GetMapping("/vlog/scraps")
	public ResponseEntity<List<BookmarkDTO>> getUserVlogBookmarks(HttpServletRequest request) {
		return getUserBookmarksByCategory(request, "vlog");
	}

	private ResponseEntity<List<BookmarkDTO>> getUserBookmarksByCategory(HttpServletRequest request, String category) {
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

		List<Bookmark> bookmarks = user.getBookmarks().stream()
			.filter(bookmark -> bookmark.getBoard().getCategoryG().getCategoryName().equalsIgnoreCase(category))
			.collect(Collectors.toList());

		if (bookmarks.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<BookmarkDTO> bookmarkDTOs = bookmarks.stream()
			.map(bookmark -> {
				BookmarkDTO bookmarkDto = new BookmarkDTO();
				bookmarkDto.setId(bookmark.getId());
				bookmarkDto.setBookmark(bookmark.getBookmark());
				bookmarkDto.setBoardId(bookmark.getBoard().getId());  // 게시글 ID 설정

				return bookmarkDto;
			})
			.collect(Collectors.toList());

		return ResponseEntity.ok(bookmarkDTOs);
	}

	@GetMapping("/blog/recently")
	public ResponseEntity<List<BoardDTO>> getRecentlyViewedBlogBoards(HttpServletRequest request) {
		return getRecentlyViewedBoardsByCategory(request, "blog");
	}

	@GetMapping("/vlog/recently")
	public ResponseEntity<List<BoardDTO>> getRecentlyViewedVlogBoards(HttpServletRequest request) {
		return getRecentlyViewedBoardsByCategory(request, "vlog");
	}

	private ResponseEntity<List<BoardDTO>> getRecentlyViewedBoardsByCategory(HttpServletRequest request, String category) {
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

		List<Click> clicks = clickRepository.findByUser(user);

		if (clicks.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<BoardDTO> boardDTOs = clicks.stream()
			.map(click -> click.getBoard())
			.filter(board -> board != null && category.equalsIgnoreCase(board.getCategoryG().getCategoryName()))
			.map(this::convertToDto)
			.collect(Collectors.toList());

		return ResponseEntity.ok(boardDTOs);
	}

	private BoardDTO convertToDto(Board board){
		return new BoardDTO(board);
	}
}
