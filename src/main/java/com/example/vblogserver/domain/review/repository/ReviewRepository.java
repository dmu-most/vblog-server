package com.example.vblogserver.domain.review.repository;

import com.example.vblogserver.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBoardId(Long boardId);
}