package com.example.vblogserver.domain.bookmark.controller;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.bookmark.dto.BookmarkDTO;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.bookmark.repository.FolderRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.jwt.util.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookmarkController {
    private final BookmarkRepository bookmarkRepository;
    private final JwtService jwtService;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final BoardRepository boardRepository;

    public BookmarkController(BookmarkRepository bookmarkRepository, JwtService jwtService, BoardService boardService, UserRepository userRepository, FolderRepository folderRepository, BoardRepository boardRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.jwtService = jwtService;
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.boardRepository = boardRepository;
    }

    // 스크랩 정보 insert
    @PostMapping("/bookmark/{contentId}/{folderId}")
    public ResponseEntity<Map<String, Object>> clickOnBookMark(HttpServletRequest request,
        @PathVariable Long contentId,
        @PathVariable Long folderId) {
        Board board = boardRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("Board not found"));

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        board.setFolder(folder);

        boardRepository.save(board);

        return ClickBookMark(request, contentId, folderId);
    }

    public ResponseEntity<Map<String, Object>> ClickBookMark(HttpServletRequest request, Long contentId, Long folderId) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        // 액세스 토큰이 존재하고 유효하다면
        if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
            String userId = jwtService.extractId(accessTokenOpt.get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출
            //BoardID 로 게시글 조회
            Board board = boardService.getBoardById(contentId);

            if (board == null) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", "게시글이 존재하지 않습니다"));
            }

            //LoginID 로 userID 조회
            User user;
            try {
                user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", userId+"을 찾을 수 없습니다"));
            }

            Optional<Folder> optFolder = folderRepository.findById(folderId);

            if (!optFolder.isPresent()) {
                return ResponseEntity.ok().body(Map.of("result", false,"reason", "폴더가 존재하지 않습니다."));
            }

            if (!optFolder.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.ok().body(Map.of("result", false,"reason", "폴더에 접근 권한이 없습니다."));
            }

            Bookmark newBookmark = Bookmark.builder()
                .board(board)
                .user(user)
                .folder(optFolder.get())
                .build();

                Bookmark saveBookMark = bookmarkRepository.save(newBookmark);

                //찜 저장 성공 시 true, 실패 시 false
                if (saveBookMark != null) {
                    return ResponseEntity.ok().body(Map.of("result", true, "reason", "저장 성공"));
                } else {
                    return ResponseEntity.ok().body(Map.of("result", false, "reason", "저장 실패"));
                }
        } else {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "유효하지 않은 액세스 토큰입니다."));
        }

    }


}
