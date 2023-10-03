package com.example.vblogserver.domain.board.controller.main;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MainListController {

    private final BoardRepository boardRepository;

    @Autowired
    public MainListController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping("/vlog/list")
    public List<MainBoardDTO> getVlogData() {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        List<Board> boards = boardRepository.findByCategoryG(categoryG);
        int limit = 20;
        List<MainBoardDTO> clientDataDTOs = boards.stream()
                .limit(limit)
                .map(this::convertToClientDataDTO)
                .collect(Collectors.toList());

        return clientDataDTOs;
    }
    @GetMapping("/blog/list")
    public List<MainBoardDTO> getBlogData() {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(2L);
        List<Board> boards = boardRepository.findByCategoryG(categoryG);
        int limit = 20;
        List<MainBoardDTO> clientDataDTOs = boards.stream()
                .limit(limit)
                .map(this::convertToClientDataDTO)
                .collect(Collectors.toList());

        return clientDataDTOs;
    }
    // Board 엔티티를 원하는 형태의 DTO로 변환
    private MainBoardDTO convertToClientDataDTO(Board board) {
        MainBoardDTO clientDataDTO = new MainBoardDTO();
        LocalDate ContentDate = board.getCreatedDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDateTime = ContentDate.format(formatter);
        clientDataDTO.setContentDate(formattedDateTime);
        clientDataDTO.setContentTitle(board.getTitle());
        clientDataDTO.setUserName(board.getWriter());
        clientDataDTO.setContent(board.getDescription());
        clientDataDTO.setHashtags(board.getHashtag());
        clientDataDTO.setContentId(board.getId());
        clientDataDTO.setImgurl(board.getThumbnails());
        clientDataDTO.setHeart(board.getLikeCount());
        if (board.getReviewCount() != null) {
            clientDataDTO.setReview(board.getReviewCount());
        }


        return clientDataDTO;
    }
}