package com.example.vblogserver.domain.board.controller;

import com.example.vblogserver.domain.board.dto.BannerDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BannerController {

    private final BoardRepository boardRepository;

    public BannerController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }


    @GetMapping("/vlog/banner")
    public BannerDTO getVlogBennerData() {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        Optional<Board> boardOptional = boardRepository.findTopByCategoryGOrderByLikeCountDesc(categoryG);
        Board board = boardOptional.orElse(null);
        return BannerDTO.fromBoard(board);
    }
    @GetMapping("/blog/banner")
    public BannerDTO getBlogBennerData() {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(2L);
        Optional<Board> boardOptional = boardRepository.findTopByCategoryGOrderByLikeCountDesc(categoryG);
        Board board = boardOptional.orElse(null);
        return BannerDTO.fromBoard(board);
    }

}
