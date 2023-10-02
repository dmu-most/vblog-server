package com.example.vblogserver.domain.review.dto;

import com.example.vblogserver.domain.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SeleteReviewDTO {
    private Long reviewId;
    private String content;
    private String reviewDate;
    private String userId;
    private float grade;

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

    public String getReviewDate() {
        return reviewDate;
    }

    public void setCreatedDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }
}