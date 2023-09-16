package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.review.controller.ReviewService;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class TmpReview {

    private final ReviewService reviewService;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public TmpReview(ReviewService reviewService, BoardService boardService, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    public String updateTmpReview() {
        //1,11,21,... ID값을 가진 게시글들에 리뷰 작성
        for(int i = 1; i < 256; i += 10) {
            //게시글 한건당 두건의 리뷰 작성
            for(int y = 0; y < 2; y++) {
                Long int_to_long = (long) i;
                Board board = boardService.getBoardById(int_to_long);

                if (board == null) {
                    System.out.println("리뷰 저장 실패. 게시글이 존재하지 않음");
                    return "리뷰 저장 실패. 게시글이 존재하지 않음.";
                }
                String content = "";
                String userId = "testuser";
                if(y == 0) content = "진짜 재미있어요 !";
                else content = "도움이 많이 됬어요~~";
                float grade;
                if(y == 0) grade = 4.5f;
                else grade = 3.0f;




                //user ID 로 계정 조회

                User user;

                try {
                    user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
                } catch (IllegalArgumentException e) {
                    System.out.println("userID를 찾을 수 없습니다(Review)");
                    return "userID를 찾을 수 없습니다(Review)";
                }


                Review newReview = Review.builder()
                        .content(content)
                        .board(board)
                        .user(user)
                        .grade(grade)
                        .build();

                Review saveReview = reviewRepository.save(newReview);

                if (saveReview != null) {
                    System.out.println("리뷰 임시 데이터 저장 성공");
                    //return "리뷰 임시 데이터 저장 성공";
                } else {
                    System.out.println("리뷰 임시 데이터 저장 실패");
                    return "리뷰 임시 데이터 저장 실패";
                }
            }
        }
        return "임시데이터 생성 완료";
    }
}
