package com.example.vblogserver.domain.review.controller;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BoardRepository boardRepository) {
        this.reviewRepository = reviewRepository;
        this.boardRepository = boardRepository;
    }

    public List<Review> getReviewByBoardId(Long boardId) {
        return reviewRepository.findByBoardId(boardId);
    }


}