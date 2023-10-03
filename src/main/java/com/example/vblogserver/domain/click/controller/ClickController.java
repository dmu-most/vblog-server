package com.example.vblogserver.domain.click.controller;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.click.repository.ClickRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ClickController {
    private final JwtService jwtService;
    private final ClickRepository clickRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;

    public ClickController(JwtService jwtService, ClickRepository clickRepository, UserRepository userRepository, BoardService boardService) {
        this.jwtService = jwtService;
        this.clickRepository = clickRepository;
        this.userRepository = userRepository;
        this.boardService = boardService;
    }

    // 게시글 클릭 여부 저장
    @PostMapping("/click/{contentId}")
    public ResponseEntity<Map<String, Object>> clickUser(HttpServletRequest request, @PathVariable Long contentId) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        // 액세스 토큰이 존재하고 유효하다면
        if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
            String userId = jwtService.extractId(accessTokenOpt.get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

            User user;
            try {
                user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
                System.out.println(user.getId());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", userId + "을 찾을 수 없습니다"));
            }

            // BoardID 로 게시글 조회
            Board board = boardService.getBoardById(contentId);

            // 이미 클릭한 내역이 있는지 확인
            boolean hasClicked = clickRepository.existsByBoardAndUser(board, user);

            if (hasClicked) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", "이미 클릭한 게시글입니다."));
            }
            // Click 여부 저장
            Click setclick = Click.builder()
                    .board(board)
                    .user(user)
                    .build();
            Click saveClick = clickRepository.save(setclick);
            // click 여부 저장 성공 시 true, 실패 시 false
            if (saveClick != null) {
                return ResponseEntity.ok().body(Map.of("result", true, "reason", "성공"));
            } else {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", "실패"));
            }
        } else {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "유효하지 않은 액세스 토큰입니다."));
        }
    }
}
