package com.example.vblogserver.domain.bookmark.controller;

import com.example.vblogserver.domain.bookmark.dto.BoardResponseDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.bookmark.dto.FolderResponseDTO;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.bookmark.repository.FolderRepository;
import com.example.vblogserver.domain.user.dto.PageResponseDto;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.jwt.util.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FolderController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FolderRepository folderRepository;
    private final BookmarkRepository bookmarkRepository;

    /*
    1. /folders : 게시글 디테일 페이지에서 폴더 생성
    2. /folders/vlog : 마이페이지에서 브이로그 폴더 생성
    3. /folders/blog : 마이페이지에서 블로그 폴더 생성
     */
    @PostMapping("/folders")
    public ResponseEntity<FolderResponseDTO> createFolder(HttpServletRequest request, @RequestBody Folder folder) {
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

        String userId = loginIdOpt.get();

        User owner = userRepository.findByLoginId(userId)
                .orElseThrow(() -> new NotFoundException(userId + "을 찾을 수 없습니다"));

        // 동일한 type의 폴더 중에서 동일한 이름을 가진 폴더가 있는지 검사
        Optional<Folder> duplicateFolder = folderRepository.findByTypeAndNameAndUser(folder.getType(), folder.getName(), owner);
        if (duplicateFolder.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 중복되는 이름의 폴더가 있으면 409 Conflict 응답 반환
        }

        folder.setUser(owner);

        Folder createdFolder = folderRepository.save(folder);

        FolderResponseDTO response = convertToDto(createdFolder);

        // 해당 폴더에 연결된 게시물들을 반환
        List<BoardResponseDTO> boardDtos =
                createdFolder.getBoards().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
        response.setBoards(boardDtos);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/folders/vlog")
    public ResponseEntity<FolderResponseDTO> createVlogFolder(HttpServletRequest request, @RequestBody Folder folder) {
        folder.setType("vlog");
        return createFolder(request, folder);
    }

    @PostMapping("/folders/blog")
    public ResponseEntity<FolderResponseDTO> createBlogFolder(HttpServletRequest request, @RequestBody Folder folder) {
        folder.setType("blog");
        return createFolder(request, folder);
    }

    // vlog, blog 별 스크랩 조회
    @GetMapping("/myinfo/scraps/vlog")
    public ResponseEntity<List<FolderResponseDTO>> getVlogFolders() {
        return getFoldersByType("vlog");
    }

    @GetMapping("/myinfo/scraps/blog")
    public ResponseEntity<List<FolderResponseDTO>> getBlogFolders() {
        return getFoldersByType("blog");
    }

    private ResponseEntity<List<FolderResponseDTO>> getFoldersByType(String type) {
        List<Folder> folders = folderRepository.findByType(type);

        List<FolderResponseDTO> folderDtos = folders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(folderDtos);
    }

    private FolderResponseDTO convertToDto(Folder folder){
        FolderResponseDTO dto = new FolderResponseDTO();
        dto.setId(folder.getId());
        dto.setName(folder.getName());
        dto.setType(folder.getType());
        dto.setUserId(folder.getUser().getId());

        List<Bookmark> bookmarksInFolder = bookmarkRepository.findByFolder(folder);
        List<BoardResponseDTO> boardDtos =
                bookmarksInFolder.stream()
                        .map(bookmark -> this.convertToDto(bookmark.getBoard()))
                        .collect(Collectors.toList());

        dto.setBoards(boardDtos);
        return dto;
    }

    @GetMapping("/myinfo/folders/{folderId}")
    public ResponseEntity<PageResponseDto<BoardResponseDTO>> getFolder(@PathVariable Long folderId,
                                                               @RequestParam(defaultValue = "0") int page) {
        Folder folder = folderRepository.findById(folderId).orElse(null);

        if (folder == null) {
            return ResponseEntity.notFound().build();
        }

        PageRequest pageRequest = PageRequest.of(page, 5); // 페이지당 사이즈 : 5개
        Page<Board> boards = boardRepository.findByFolderAndCategoryG_CategoryNameIgnoreCase(
                folder,
                folder.getType(),
                pageRequest);

        if (boards.isEmpty()) {
            return ResponseEntity.ok(new PageResponseDto<>(new ArrayList<>(), page, 5, 0));
        }

        List<BoardResponseDTO> boardDTOs =
                boards.getContent().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());

        PageResponseDto<BoardResponseDTO> response =
                new PageResponseDto<>(boardDTOs,
                        boards.getNumber(),
                        boards.getSize(),
                        boards.getTotalElements());

        return ResponseEntity.ok(response);
    }

    private BoardResponseDTO convertToDto(Board board){
        return new BoardResponseDTO(board);
    }

}
