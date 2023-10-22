package com.example.vblogserver.domain.user.controller.myinfo;

import com.example.vblogserver.domain.board.dto.BoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.bookmark.dto.FolderResponseDTO;
import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.bookmark.repository.FolderRepository;
import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.click.repository.ClickRepository;
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
public class BookmarksController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FolderRepository folderRepository;


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

        folder.setUser(owner);

        Folder createdFolder = folderRepository.save(folder);

        FolderResponseDTO response = new FolderResponseDTO();
        response.setId(createdFolder.getId());
        response.setName(createdFolder.getName());
        response.setType(createdFolder.getType());
        response.setUserId(createdFolder.getUser().getId()); // set owner's id
        response.setBoards(createdFolder.getBoards());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/myinfo/folders/vlog")
    public ResponseEntity<List<FolderResponseDTO>> getVlogFolders() {
        return getFoldersByType("vlog");
    }

    @GetMapping("/myinfo/folders/blog")
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
        // Boards are not included in this DTO.
        return dto;
    }


    @GetMapping("/myinfo/folders/{folderId}")
    public ResponseEntity<PageResponseDto<BoardDTO>> getFolder(@PathVariable Long folderId,
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

        List<BoardDTO> boardDTOs =
                boards.getContent().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());

        PageResponseDto<BoardDTO> response =
                new PageResponseDto<>(boardDTOs,
                        boards.getNumber(),
                        boards.getSize(),
                        boards.getTotalElements());

        return ResponseEntity.ok(response);
    }

    private BoardDTO convertToDto(Board board){
        return new BoardDTO(board);
    }

}
