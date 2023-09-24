package com.example.vblogserver.domain.review.repository;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBoardId(Long boardId);
    List<Review> findReviewsByBoard(Board board);
    Page<Review> findReviewsByUserAndBoard_CategoryG_CategoryNameIgnoreCase(User user, String categoryName, Pageable pageable);

}