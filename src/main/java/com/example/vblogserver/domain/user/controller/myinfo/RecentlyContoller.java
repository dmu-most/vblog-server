package com.example.vblogserver.domain.user.controller.myinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.vblogserver.domain.user.dto.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myinfo/recently")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecentlyContoller {
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ClickRepository clickRepository;
	private final BoardRepository boardRepository;

	@GetMapping("/blog")
	public ResponseEntity<PageResponseDto<BoardDTO>> getRecentlyViewedBlogBoards(
			HttpServletRequest request,
			@RequestParam(defaultValue = "0") int page) {
		return getRecentlyViewedBoardsByCategory(request,"blog", page);
	}

	@GetMapping("/vlog")
	public ResponseEntity<PageResponseDto<BoardDTO>> getRecentlyViewedVlogBoards(
			HttpServletRequest request,
			@RequestParam(defaultValue = "0") int page) {
		return getRecentlyViewedBoardsByCategory(request,"vlog", page);
	}

	private ResponseEntity<PageResponseDto<BoardDTO>> getRecentlyViewedBoardsByCategory(
			HttpServletRequest request,
			String category,
			int page) {

		Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

		if (accessTokenOpt.isEmpty() || !jwtService.isTokenValid(accessTokenOpt.get())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		Optional<String> loginIdOpt = jwtService.extractId(accessTokenOpt.get());


		if (loginIdOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		User user = userRepository.findByLoginId(loginIdOpt.get()).orElseThrow(() -> new RuntimeException("User not found"));

		PageRequest pageRequest = PageRequest.of(page, 5); // 페이지당 사이즈 : 5개
		Page<Click> clicks = clickRepository.findByUser(user, pageRequest);

		List<Long> boardIds = clicks.getContent().stream()
				.map(click -> click.getBoard().getId())
				.collect(Collectors.toList());

		List<Board> boards = boardRepository.findByIdInAndCategoryG_CategoryNameIgnoreCase(boardIds, category);

		if (boards.isEmpty()) {
			return ResponseEntity.ok(new PageResponseDto<>(new ArrayList<>(), page, 5, 0));
		}

		List<BoardDTO> boardDTOs = boards.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());

		PageResponseDto<BoardDTO> response = new PageResponseDto<>();
		response.setContent(boardDTOs);
		response.setPageNumber(clicks.getNumber());
		response.setPageSize(clicks.getSize());
		response.setTotalElements(clicks.getTotalElements());

		return ResponseEntity.ok(response);
	}

		private BoardDTO convertToDto(Board board){
		return new BoardDTO(board);
	}
}
