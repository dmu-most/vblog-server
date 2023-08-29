package com.example.vblogserver.domain.review;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ReviewEventListener {
    private final BoardService boardService;
    private final ReviewRepository reviewRepository;

    public ReviewEventListener(BoardService boardService, ReviewRepository reviewRepository) {
        this.boardService = boardService;
        this.reviewRepository = reviewRepository;
    }


    //평점의 평균을 구해 board 에 저장하는 Event
    @EventListener
    @Transactional
    public void handleReviewEvent(ReviewEvent event){
        Long boardId = event.getBoardId();
        Board board = boardService.getBoardById(boardId);
        if(board == null){
            return;
        }

        List<Review> reviews = reviewRepository.findByBoardId(boardId);
        float totalGrade = (float) reviews.stream()
                .mapToDouble(Review::getGrade)
                .sum();
        float averageGrade = totalGrade / reviews.size();
        board.setGrade(averageGrade);

    }
}
