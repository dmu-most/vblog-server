package com.example.vblogserver.domain.board.controller.main;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.main.DTOConvertServcie;
import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LikeListController {

    private final BoardRepository boardRepository;
    private final DTOConvertServcie dtoConvertServcie;

    public LikeListController(BoardRepository boardRepository, DTOConvertServcie dtoConvertServcie) {
        this.boardRepository = boardRepository;
        this.dtoConvertServcie = dtoConvertServcie;
    }

    //vlog 좋아요 순으로 조회
    @GetMapping("/vlog/likelist")
    public List<MainBoardDTO> getVlogLikeList(){
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        List<Board> boards = boardRepository.findByCategoryGOrderByLikeCountDesc(categoryG);
        return dtoConvertServcie.BoardToMainBoard(boards);
    }
    //blog 좋아요 순으로 조회
    @GetMapping("/blog/likelist")
    public List<MainBoardDTO> getBlogLikeList(){
        CategoryG categoryG = new CategoryG();
        categoryG.setId(2L);
        List<Board> boards = boardRepository.findByCategoryGOrderByLikeCountDesc(categoryG);
        return dtoConvertServcie.BoardToMainBoard(boards);
    }

}
