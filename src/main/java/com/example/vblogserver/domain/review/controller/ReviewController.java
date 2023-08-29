package com.example.vblogserver.domain.review.controller;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.review.dto.ReviewDTO;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewController(ReviewService reviewService, BoardService boardService, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    // 리뷰 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<List<ReviewDTO>> readReview(@PathVariable Long boardId) {
        List<Review> reviews = reviewService.getReviewByBoardId(boardId);

        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> {
                    ReviewDTO reviewDTO = new ReviewDTO();
                    reviewDTO.setId(review.getId());
                    reviewDTO.setContent(review.getContent());
                    reviewDTO.setCreatedDate(review.getCreatedDate());
                    reviewDTO.setUserEmail(review.getUser());
                    reviewDTO.setGrade(review.getGrade());
                    return reviewDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs);

    }

    // 리뷰 작성
    @PostMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> createReview(@PathVariable Long boardId, @RequestBody Map<String, String> request) {
        //BoardID 로 게시글 조회
        Board board = boardService.getBoardById(boardId);

        if (board == null) {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "게시글이 존재하지 않습니다"));
        }
        String content = request.get("content");
        String userEmail = request.get("userEmail");
        float grade = Float.parseFloat(request.get("grade"));

        //user Email 로 계정 조회
        User user;
        try {
            user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException(userEmail + "을 찾을 수 없습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", userEmail+"을 찾을 수 없습니다"));
        }
        Review newReview = Review.builder()
                .content(content)
                .board(board)
                .user(user)
                .grade(grade)
                .build();

        Review saveReview = reviewRepository.save(newReview);
        //리뷰 저장 성공 시 true, 실패 시 false
        if(saveReview != null){
            return ResponseEntity.ok().body(Map.of("result", true, "reason", "저장 성공"));
        } else{
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "저장 실패"));
        }
    }

    //리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> updateReview(@PathVariable Long reviewId, @RequestBody Map<String, String> request) {
        // 리뷰 ID로 리뷰 조회
        Review review = reviewRepository.findById(reviewId).orElse(null);

        // 수정할 리뷰가 없을 경우
        if (review == null) {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "수정할 리뷰가 존재하지 않습니다"));
            //return ResponseEntity.ok("{\"result\" : false,\"reason\" : \"수정할 리뷰가 존재하지 않습니다\"}");
        }
        String newContent = request.get("content");
        float newGrade = Float.parseFloat(request.get("Grade"));

        // 수정된 리뷰 내용+평점으로 저장
        review.setContent(newContent);
        review.setGrade(newGrade);
        Review updatedReview = reviewRepository.save(review);

        if (updatedReview != null) {
            return ResponseEntity.ok().body(Map.of("result", true, "reason", "수정 성공"));
        } else {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "수정 실패"));
        }
    }


    //리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long reviewId) {
        // 리뷰 ID로 리뷰 조회
        Review review = reviewRepository.findById(reviewId).orElse(null);

        // 삭제할 리뷰가 없을 경우
        if (review == null) {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "삭제할 리뷰가 존재하지 않습니다"));
        }

        // 리뷰 삭제
        reviewRepository.delete(review);
        return ResponseEntity.ok().body(Map.of("result", true, "reason", "삭제 성공"));
    }
}
