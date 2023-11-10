package com.example.vblogserver.domain.user.controller.myinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.vblogserver.domain.board.dto.BoardRecentlyResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.vblogserver.domain.board.dto.BoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.click.repository.ClickRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/myinfo/recently")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecentlyContoller {
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ClickRepository clickRepository;
	private final BoardRepository boardRepository;

	public RecentlyContoller(JwtService jwtService, UserRepository userRepository, ClickRepository clickRepository, BoardRepository boardRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.clickRepository = clickRepository;
		this.boardRepository = boardRepository;
	}

	@GetMapping("/blog")
	public ResponseEntity<List<BoardRecentlyResponseDTO>> getRecentlyViewedBlogBoards(HttpServletRequest request) {
		return getRecentlyViewedBoardsByCategory(request,"blog");
	}

	@GetMapping("/vlog")
	public ResponseEntity<List<BoardRecentlyResponseDTO>> getRecentlyViewedVlogBoards(HttpServletRequest request) {
		return getRecentlyViewedBoardsByCategory(request,"vlog");
	}

	private ResponseEntity<List<BoardRecentlyResponseDTO>> getRecentlyViewedBoardsByCategory(HttpServletRequest request, String category) {

		Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

		if (accessTokenOpt.isEmpty() || !jwtService.isTokenValid(accessTokenOpt.get())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		Optional<String> loginIdOpt = jwtService.extractId(accessTokenOpt.get());


		if (loginIdOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		User user = userRepository.findByLoginId(loginIdOpt.get()).orElseThrow(() -> new RuntimeException("User not found"));

		// 날짜순 상위 12개의 클릭 기록만 가져온다.
		List<Click> clicks = clickRepository.findTop12ByUserOrderByClickedDateDesc(user);

		List<Long> boardIds = clicks.stream()
				.filter(click -> click.getBoard() != null) // Board 가 null 이 아닌 경우만 처리
				.map(click -> click.getBoard().getId())
				.collect(Collectors.toList());

		List<Board> boards = boardRepository.findByIdInAndCategoryG_CategoryNameIgnoreCase(boardIds, category);

		if (boards.isEmpty()) {
			return ResponseEntity.ok(new ArrayList<>());
		}

		List<BoardRecentlyResponseDTO> responseDTOS = boards.stream()
				.map(BoardRecentlyResponseDTO::new)
				.collect(Collectors.toList());

		Collections.reverse(responseDTOS); // 최신순으로 반환 하기 위해 리스트를 역순으로 정렬

		return ResponseEntity.ok(responseDTOS);
	}

	private BoardDTO convertToDto(Board board){
		return new BoardDTO(board);
	}
}


