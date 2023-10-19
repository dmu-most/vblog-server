package com.example.vblogserver.domain.board.controller.main;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HashtagController {
    private final BoardRepository boardRepository;

    public HashtagController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping("/vlog/hashtags")
    public List<String> searchVlog() {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        return searchHashtags(categoryG);
    }

    @GetMapping("/blog/hashtags")
    public List<String> searchBlog() {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        return searchHashtags(categoryG);
    }

    public List<String> searchHashtags(CategoryG vblog){
        List<Board> searchResults = boardRepository.findByCategoryGOrderByLikeCountDesc(vblog);
        List<String> hashtags = searchResults.stream()
                .map(Board::getHashtag)
                .filter(hashtag -> hashtag!=null && !hashtag.isEmpty())
                .map(tag -> tag.split("#")[1])
                .limit(10)
                .collect(Collectors.toList());

        return hashtags;
    }
}
