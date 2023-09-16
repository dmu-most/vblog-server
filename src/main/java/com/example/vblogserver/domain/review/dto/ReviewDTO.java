package com.example.vblogserver.domain.review.dto;

import com.example.vblogserver.domain.user.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewDTO {
    //리뷰 ID
    private Long id;
    //리뷰 내용
    private String content;
    //리뷰 작성 일자
    private String createdDate;
    //작성자
    private String userId;
    //평점
    private float grade;

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


}
