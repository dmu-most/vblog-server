package com.example.vblogserver.domain.board.controller;

import com.example.vblogserver.domain.board.dto.BoardDetailDTO;
import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.review.dto.ReviewDTO;
import com.example.vblogserver.domain.review.entity.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
public class DetailController {
    private final BoardRepository boardRepository;

    public DetailController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailDTO> readDetailBoard(@PathVariable Long boardId) {
        // boardId 게시글이 없을 경우
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("조회된 게시글이 없습니다."));

        // 랭킹 계산 (좋아요 순으로 정렬)
        List<Board> findAllBoards = boardRepository.findAll();
        findAllBoards.sort(Comparator.comparing(Board::getLikeCount).reversed());
        int rank = findAllBoards.indexOf(board) + 1;

        // return : BoardDetailDTO
        BoardDetailDTO boardDetailDTO = new BoardDetailDTO();
        boardDetailDTO.setContentId(board.getId());
        boardDetailDTO.setContentTitle(board.getTitle());
        boardDetailDTO.setContent(board.getDescription());
        boardDetailDTO.setUserName(board.getWriter());
        boardDetailDTO.setHashtags(Arrays.asList(board.getHashtag().split("#")));
        boardDetailDTO.setRank(rank);
        boardDetailDTO.setGrade(board.getGrade());
        boardDetailDTO.setHeart(board.getLikeCount());
        boardDetailDTO.setHate(board.getDisLikeCount());
        boardDetailDTO.setImgurl(board.getThumbnails());
        boardDetailDTO.setLink(board.getLink());

        return ResponseEntity.ok(boardDetailDTO);
    }
}
