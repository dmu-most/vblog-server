package com.example.vblogserver.domain.board.controller.main;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.click.repository.ClickRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.vblogserver.domain.board.dto.CategoryMDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ClickBasedController {
    private final ClickRepository clickRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    private final JwtService jwtService;
    private final UserRepository userRepository;


    public ClickBasedController(ClickRepository clickRepository, BoardRepository boardRepository, BoardService boardService, JwtService jwtService, UserRepository userRepository) {
        this.clickRepository = clickRepository;
        this.boardRepository = boardRepository;
        this.boardService = boardService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public List<Click> getClicksByUserId(User user) {
        return clickRepository.findByUser(user);
    }

    //사용자 맞춤 추천 게시글 조회 (게시글 클릭 정보를 활용하여 조회)

    @GetMapping("/vlog/userBase")
    public ResponseEntity<Map<String, Object>> getUserBasedVlogList(HttpServletRequest request) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        // 액세스 토큰이 존재하고 유효하다면
        if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
            String userId = jwtService.extractId(accessTokenOpt.get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

            //LoginID 로 userID 조회
            User user;
            try {
                user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
                if (getCategoryTop2(user)==null) return ResponseEntity.ok().body(Map.of("result", false, "reason", "2개 이상의 카테고리를 조회하지 못했습니다. 더 많은 게시글을 클릭해야합니다."));
                else return ResponseEntity.ok().body(Map.of("result", true, "reason", getCategoryTop2(user)));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", userId + "을 찾을 수 없습니다"));
            }
        } else {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "유효하지 않은 액세스 토큰입니다."));
        }
    }

    @GetMapping("/blog/userBase")
    public ResponseEntity<Map<String, Object>> getUserBasedBlogList(HttpServletRequest request) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        // 액세스 토큰이 존재하고 유효하다면
        if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
            String userId = jwtService.extractId(accessTokenOpt.get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

            //LoginID 로 userID 조회
            User user;
            try {
                user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
                if (getCategoryTop2(user)==null) return ResponseEntity.ok().body(Map.of("result", false, "reason", "2개 이상의 카테고리를 조회하지 못했습니다. 더 많은 게시글을 클릭해야합니다."));
                else return ResponseEntity.ok().body(Map.of("result", true, "reason", getCategoryTop2(user)));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", userId + "을 찾을 수 없습니다"));
            }
        } else {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "유효하지 않은 액세스 토큰입니다."));
        }
    }
    //사용자가 클릭한 게시글의 카테고리 중 가장 많이 조회된 게시글의 카테고리 TOP2 조회
    public List<CategoryMDTO> getCategoryTop2(User user){
        // 특정 사용자가 클릭한 게시글 리스트 조회
        List<Click> clicksByUser = clickRepository.findByUser(user);

        // 각 게시글의 categoryM을 count하기 위한 Map을 생성
        Map<CategoryM, Long> categoryCountMap = clicksByUser.stream()
                .map(Click::getBoard)
                .map(Board::getCategoryM)
                .collect(Collectors.groupingBy(categoryM -> categoryM, Collectors.counting()));

        // CategoryM을 count 하여 내림차순으로 정렬
        List<Map.Entry<CategoryM, Long>> sortedCategories = categoryCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .collect(Collectors.toList());

        // 상위 2개의 카테고리 조회
        List<CategoryMDTO> topTwoCategories;
        // 조회된 카테고리가 2개 이상인 경우
        if (sortedCategories.size() >= 2) {
            // 상위 2개의 카테고리를 선택합니다.
            topTwoCategories = sortedCategories.stream()
                    .limit(2)
                    .map(entry -> new CategoryMDTO(entry.getKey().getCategoryName()))
                    .collect(Collectors.toList());
            return topTwoCategories;
        } else {
            return null;
        }

    }
}
