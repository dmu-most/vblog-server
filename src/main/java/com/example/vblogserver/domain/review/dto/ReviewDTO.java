package com.example.vblogserver.domain.review.dto;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewDTO {
    //리뷰 ID
    private Long reviewId;
    //리뷰 내용
    private String content;
    //리뷰 작성 일자
    private String createdDate;

    //평점
    private float grade;
    // 블로그인지 브이로그인지 구분
    private String category;
    // 게시글 ID
    private Long boardId;

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public float getGrade() {
        return grade;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    public void setCategory(String category) {this.category = category; }

    public String getCategory() {return category;}

    public void setBoardId(Long boardId) {this.boardId = boardId; }
    public Long getBoardId() { return boardId; }

}
