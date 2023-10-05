package com.example.vblogserver.domain.review.dto;

import com.example.vblogserver.domain.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SeleteReviewDTO {
    private Long contentId;
    private Long reviewId;
    private String reviewContent;
    private String reviewDate;
    private String userName;
    private float grade;

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setCreatedDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
}
