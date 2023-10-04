package com.example.vblogserver.domain.bookmark.controller;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.bookmark.dto.BookmarkDTO;
import com.example.vblogserver.domain.bookmark.dto.BookmarkFolderDTO;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.entity.BookmarkFolder;
import com.example.vblogserver.domain.bookmark.repository.BookmarkFolderRepository;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
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
    private final BookmarkFolderRepository bookmarkFolderRepository;
    private final BoardRepository boardRepository;

    public BookmarkController(BookmarkRepository bookmarkRepository, JwtService jwtService, BoardService boardService, UserRepository userRepository, BookmarkFolderRepository bookmarkFolderRepository, BoardRepository boardRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.jwtService = jwtService;
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.bookmarkFolderRepository = bookmarkFolderRepository;
        this.boardRepository = boardRepository;
    }

    // 찜 정보 insert
    @PostMapping("/bookmark/{contentId}/{folderId}")
    public ResponseEntity<Map<String, Object>> clickOnBookMark(HttpServletRequest request,
        @PathVariable Long contentId,
        @PathVariable Long folderId) {
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

            Optional<BookmarkFolder> optBookmarkFolder = bookmarkFolderRepository.findById(folderId);

            if (!optBookmarkFolder.isPresent()) {
                return ResponseEntity.ok().body(Map.of("result", false,"reason", "폴더가 존재하지 않습니다."));
            }

            if (!optBookmarkFolder.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.ok().body(Map.of("result", false,"reason", "폴더에 접근 권한이 없습니다."));
            }

            Bookmark newBookmark = Bookmark.builder()
                .board(board)
                .user(user)
                .bookmarkFolder(optBookmarkFolder.get())
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

    // 폴더 생성
    @PostMapping("/folder")
    public ResponseEntity<Map<String, Object>> createBookmarkFolder(HttpServletRequest request, @RequestBody Map<String, String> folderInfo) {
        String userId = jwtService.extractId(jwtService.extractAccessToken(request).get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

        User user;
        try {
            user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", userId+"을 찾을 수 없습니다"));
        }

        String folderName = folderInfo.get("name");

        // 이미 동일한 이름의 폴더가 있는지 확인
        List<BookmarkFolder> existingFolders = bookmarkFolderRepository.findByNameAndUser(folderName, user);
        if (!existingFolders.isEmpty()) {
            return ResponseEntity.ok().body(Map.of("result", false,"reason", "동일한 이름의 폴더가 이미 존재합니다."));
        }


        BookmarkFolder newBookmarkFolder= new BookmarkFolder(folderName ,user,new ArrayList<>());

        bookmarkFolderRepository.save(newBookmarkFolder);

        return ResponseEntity.ok().body(Map.of("result", true,"name", newBookmarkFolder.getName()));
    }

    // 폴더 조회
    @GetMapping("/myinfo/bookmark")
    public ResponseEntity<Map<String, Object>> getUserFolders(HttpServletRequest request) {
        String userId = jwtService.extractId(jwtService.extractAccessToken(request).get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

        User user;
        try {
            user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", userId+"을 찾을 수 없습니다"));
        }

        List<BookmarkFolder> userFolders = bookmarkFolderRepository.findByUser(user);

        List<BookmarkFolderDTO> dtoList = userFolders.stream()
            .map(folder -> new BookmarkFolderDTO(folder.getId(), folder.getName()))
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        result.put("folders", dtoList);

        return ResponseEntity.ok().body(result);
    }

    // 폴더 속 찜한 게시글 조회
    @GetMapping("/myinfo/bookmark/{folderId}")
    public ResponseEntity<Map<String, Object>> getBookmarksInFolder(HttpServletRequest request, @PathVariable Long folderId) {
        String userId = jwtService.extractId(jwtService.extractAccessToken(request).get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

        User user;
        try {
            user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", userId+"을 찾을 수 없습니다"));
        }

        Optional<BookmarkFolder> optBookmarkFolder = bookmarkFolderRepository.findById(folderId);

        if (!optBookmarkFolder.isPresent()) {
            return ResponseEntity.ok().body(Map.of("result", false,"reason", "폴더가 존재하지 않습니다."));
        }

        if (!optBookmarkFolder.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok().body(Map.of("result", false,"reason", "폴더에 접근 권한이 없습니다."));
        }

        List<Bookmark> bookmarksInFolder = optBookmarkFolder.get().getBookmarks();

        List<BookmarkDTO> dtoList = bookmarksInFolder.stream()
            .map(bookmark -> new BookmarkDTO(bookmark.getId(), bookmark.getBoard().getId()))
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        result.put("bookmarks", dtoList);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/myinfo/folder/blog")
    public ResponseEntity<List<BookmarkFolderDTO>> getBlogFolders(HttpServletRequest request) {
        return getFoldersByCategory(request, "blog");
    }

    @GetMapping("/myinfo/folder/vlog")
    public ResponseEntity<List<BookmarkFolderDTO>> getVlogFolders(HttpServletRequest request) {
        return getFoldersByCategory(request, "vlog");
    }

    private ResponseEntity<List<BookmarkFolderDTO>> getFoldersByCategory(HttpServletRequest request, String category) {
        String userId = jwtService.extractId(jwtService.extractAccessToken(request).get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출

        User user;
        try {
            user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok().body(
                (List<BookmarkFolderDTO>)Map.of("result", false, "reason", userId+"을 찾을 수 없습니다"));
        }

        List<BookmarkFolder> folders = bookmarkFolderRepository.findByUser(user);

        List<Board> boards = boardRepository.findByUserIdAndCategoryG_CategoryNameIgnoreCase(user.getId(), category);

        List<Long> boardIds = boards.stream()
            .map(Board::getId)
            .collect(Collectors.toList());

        List<BookmarkFolderDTO> folderDTOs = folders.stream()
            .filter(folder -> folder.getBookmarks().stream()
                .anyMatch(bookmark -> boardIds.contains(bookmark.getBoard().getId())))
            .map(folder -> new BookmarkFolderDTO(folder))
            .collect(Collectors.toList());

        return ResponseEntity.ok(folderDTOs);
    }

}
