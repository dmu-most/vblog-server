package com.example.vblogserver.domain.user.controller.myinfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.board.dto.BoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.click.repository.ClickRepository;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myinfo/recently")
public class RecentlyContoller {
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ClickRepository clickRepository;
	private final BoardRepository boardRepository;

	// 최근 기록 (페이징 처리 : 10개)
	// TODO: 모두 보기(최대 10개)
	@GetMapping("/blog")
	public ResponseEntity<List<BoardDTO>> getRecentlyViewedBlogBoards(HttpServletRequest request) {
		return getRecentlyViewedBoardsByCategory(request,"blog");
	}

	@GetMapping("/vlog")
	public ResponseEntity<List<BoardDTO>> getRecentlyViewedVlogBoards(HttpServletRequest request) {
		return getRecentlyViewedBoardsByCategory(request,"vlog");
	}

	private ResponseEntity<List<BoardDTO>> getRecentlyViewedBoardsByCategory(HttpServletRequest request, String category) {		// 액세스 토큰 추출
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

		Page<Click> clicks = clickRepository.findByUser(user, PageRequest.of(0, 40));

		if (clicks.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<Long> boardIds = clicks.getContent().stream()
			.map(click -> click.getBoard().getId())
			.collect(Collectors.toList());

		List<Board> boards = boardRepository.findByIdInAndCategoryG_CategoryNameIgnoreCase(boardIds, category);

		List<BoardDTO> boardDTOs = boards.stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());

		return ResponseEntity.ok(boardDTOs);
	}

	private BoardDTO convertToDto(Board board){
		return new BoardDTO(board);
	}
}
