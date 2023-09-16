package com.example.vblogserver.domain.board.controller.main;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.main.DTOConvertServcie;
import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
@RestController
public class NewListController {
    private final BoardRepository boardRepository;
    private final DTOConvertServcie dtoConvertServcie;

    public NewListController(BoardRepository boardRepository, DTOConvertServcie dtoConvertServcie) {
        this.boardRepository = boardRepository;
        this.dtoConvertServcie = dtoConvertServcie;
    }
    //vlog 최신순으로 조회
    @GetMapping("/vlog/newlist")
    public List<MainBoardDTO> getVlogNewList(){
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        List<Board> boards = boardRepository.findByCategoryGOrderByCreatedDateDesc(categoryG);
        return dtoConvertServcie.BoardToMainBoard(boards);
    }
    //blog 최신순으로 조회
    @GetMapping("/blog/newlist")
    public List<MainBoardDTO> getBlogNewList(){
        CategoryG categoryG = new CategoryG();
        categoryG.setId(2L);
        List<Board> boards = boardRepository.findByCategoryGOrderByCreatedDateDesc(categoryG);
        return dtoConvertServcie.BoardToMainBoard(boards);
    }


}
