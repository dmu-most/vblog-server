package com.example.vblogserver.domain.LikeInfo.controller;

import com.example.vblogserver.domain.LikeInfo.dto.LikeInfoDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.LikeInfo.entity.LikeInfo;
import com.example.vblogserver.domain.LikeInfo.repository.LikeInfoRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class LikeInfoContoller {
    private final LikeInfoRepository likeInfoRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BoardService boardService;

    private final BoardRepository boardRepository;


    public LikeInfoContoller(LikeInfoRepository likeInfoRepository, JwtService jwtService, UserRepository userRepository, BoardService boardService, BoardRepository boardRepository) {
        this.likeInfoRepository = likeInfoRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }

    /*
     특정 게시글에 대한 좋아요, 싫어요 저장.
     좋아요, 싫어요 구분은 true, false 로 구분.
     */
    @PostMapping("/like/{contentId}")
    public String updateLikeInfo(HttpServletRequest request, @PathVariable Long contentId, @RequestBody LikeInfoDTO likeInfoDTO) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        // 액세스 토큰이 존재하고 유효하다면
        if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
            String userId = jwtService.extractId(accessTokenOpt.get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

            User user;
            try {
                user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
            } catch (IllegalArgumentException e) {
                return "로그인한 유저를 찾을 수 없습니다";
                //return ResponseEntity.ok().body(Map.of("result", false, "reason", userId + "을 찾을 수 없습니다"));
            }

            // BoardID 로 게시글 조회
            Board board = boardService.getBoardById(contentId);

            // 이미 저장된 LikeInfo 엔티티가 있는지 확인
            Optional<LikeInfo> existingLikeInfoOpt = likeInfoRepository.findByBoardAndUser(board, user);

            // 이미 저장된 LikeInfo가 있으면 업데이트, 없으면 새로 생성
            if (existingLikeInfoOpt.isPresent()) {
                // 이미 저장된 LikeInfo의 값이 좋아요(true) 인지 싫어요(false) 인지를 확인하여
                // true, false 에 따라 board 테이블의 like_count와 dis_like_count 을 재카운팅하여 업데이트
                System.out.println("싫어요 existingLikeInfoOpt.get().getLikeInfo() : " + existingLikeInfoOpt.get().getLikeInfo());
                LikeInfo existingLikeInfo = existingLikeInfoOpt.get();
                existingLikeInfo.setLikeInfo(likeInfoDTO.getLikeInfo());
                // 업데이트된 LikeInfo 저장
                LikeInfo updatedLikeInfo = likeInfoRepository.save(existingLikeInfo);
                if (updatedLikeInfo != null) {
                    int likeCount = likeInfoRepository.countByLikeInfoAndBoard(true, board);
                    int disLikeCount = likeInfoRepository.countByLikeInfoAndBoard(false, board);
                    System.out.println(board.getId());
                    board.setLikeCount(likeCount);
                    System.out.println("likeCount : "+likeCount);
                    board.setDisLikeCount(disLikeCount);
                    System.out.println("disLikeCount : "+disLikeCount);
                    boardRepository.save(board);
                    return "업데이트 성공";
                } else {
                    return "업데이트 실패";
                }


            } else {
                // 새로운 LikeInfo 생성 및 저장
                LikeInfo newLikeInfo = LikeInfo.builder()
                        .board(board)
                        .user(user)
                        .likeInfo(likeInfoDTO.getLikeInfo())
                        .build();
                LikeInfo savedLikeInfo = likeInfoRepository.save(newLikeInfo);
                if (savedLikeInfo != null) {
                    int likeCount = likeInfoRepository.countByLikeInfoAndBoard(true, board);
                    int disLikeCount = likeInfoRepository.countByLikeInfoAndBoard(false, board);
                    System.out.println(board.getId());
                    board.setLikeCount(likeCount);
                    System.out.println("likeCount : "+likeCount);
                    board.setDisLikeCount(disLikeCount);
                    System.out.println("disLikeCount : "+disLikeCount);
                    boardRepository.save(board);
                    return "저장 성공";
                } else {
                    return "저장 실패";
                }
            }
        } else {
            return "유효하지 않은 액세스 토큰입니다";
        }
    }

    // 좋아요 정보 조회
    @GetMapping("/like/{contentId}")
    public ResponseEntity<Map<String, Object>> viewLikeInfo(HttpServletRequest request, @PathVariable Long contentId) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        // 액세스 토큰이 존재하고 유효하다면
        if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
            String userId = jwtService.extractId(accessTokenOpt.get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

            User user;
            try {
                user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", userId + "을 찾을 수 없습니다"));
            }

            // BoardID 로 게시글 조회
            Board board = boardService.getBoardById(contentId);

            // 특정 게시글에 대한 좋아요, 싫어요 내역 조회
            Optional<LikeInfo> likeInfo = likeInfoRepository.findByBoardAndUser(board, user);
            LikeInfo result = likeInfo.orElse(null); // 결과가 없을 경우 null을 반환
            if (result == null) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", "조회된 내역이 없습니다."));
            } else {
                LikeInfoDTO likeInfoDTO = new LikeInfoDTO();
                likeInfoDTO.setLikeInfo(result.getLikeInfo());
                return ResponseEntity.ok().body(Map.of("result", true, "reason", likeInfoDTO));
            }

        } else {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "유효하지 않은 액세스 토큰입니다."));
        }
    }
}
