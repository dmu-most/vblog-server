package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TmpGrade {
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final ReviewRepository reviewRepository;
    public TmpGrade(BoardService boardService, BoardRepository boardRepository, ReviewRepository reviewRepository) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
        this.reviewRepository = reviewRepository;
    }

    public void updateGrade(){
        List<Board> allBoards = boardService.getAllBoards();

        for (Board board : allBoards) {
            List<Review> reviews = reviewRepository.findReviewsByBoard(board);

            if (!reviews.isEmpty()) {
                float totalGrade = 0.0f;

                // 모든 리뷰의 grade 값을 더함
                for (Review review : reviews) {
                    totalGrade += review.getGrade();
                }

                // 평균 값을 계산
                float averageGrade = totalGrade / reviews.size();

                // 게시글의 grade를 업데이트
                board.setGrade(averageGrade);
                board.setReviewCount(reviews.size());
                boardRepository.save(board);
            }
        }
    }
}
