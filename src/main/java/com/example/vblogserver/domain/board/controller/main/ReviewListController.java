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
public class ReviewListController {
    private final BoardRepository boardRepository;
    private final DTOConvertServcie dtoConvertServcie;

    public ReviewListController(BoardRepository boardRepository, DTOConvertServcie dtoConvertServcie) {
        this.boardRepository = boardRepository;
        this.dtoConvertServcie = dtoConvertServcie;
    }
    //vlog 리뷰 많은 순으로 조회
    @GetMapping("/vlog/reviewlist")
    public List<MainBoardDTO> getVlogLikeList(){
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        List<Board> boards = boardRepository.findByCategoryGOrderByReviewCountDesc(categoryG);
        return dtoConvertServcie.BoardToMainBoard(boards);
    }
    //blog 리뷰 많은 순으로 조회
    @GetMapping("/blog/reviewlist")
    public List<MainBoardDTO> getBlogLikeList(){
        CategoryG categoryG = new CategoryG();
        categoryG.setId(2L);
        List<Board> boards = boardRepository.findByCategoryGOrderByReviewCountDesc(categoryG);
        return dtoConvertServcie.BoardToMainBoard(boards);
    }

}
