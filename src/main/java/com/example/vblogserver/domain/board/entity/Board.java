package com.example.vblogserver.domain.board.entity;

import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.entity.CategoryS;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    //작성자
    private String writer;


    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    //제목
    private String title;
    //게시글 링크
    private String link;
    //내용
    @Column(length = 3000)
    private String description;
    //썸네일 이미지 링크
    @Column(length = 3000)
    private String thumbnails;
    //해시태그
    private String hashtag;

    //게시글 작성 일자
    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private String createdDate;

    //리뷰 count
    @ColumnDefault("0")
    private Integer reviewCount;
    //좋아요 count
    @ColumnDefault("0")
    private Integer likeCount;
    //싫어요 count
    @ColumnDefault("0")
    private Integer disLikeCount;
    //평점
    private Float grade;

    //대분류 카테고리 (vlog, blog)
    @ManyToOne
    @JoinColumn(name = "category_g")
    private CategoryG categoryG;
    //중분류 카테고리
    @ManyToOne
    @JoinColumn(name = "category_m")
    private CategoryM categoryM;
    //소분류 카테고리
    //(UI에서는 제거됨. Naver, Youtube API에 요청 시, 검색용 keyword 저장하는 용도로 사용 중)
    @ManyToOne
    @JoinColumn(name = "category_s")
    private CategoryS categoryS;


    @Builder
    public Board(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;

    }

    public Long getId() {
        return id;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public void setDisLikeCount(Integer disLikeCount) {
        this.disLikeCount = disLikeCount;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }

    public void setCategoryG(CategoryG categoryG) {
        this.categoryG = categoryG;
    }

    public void setCategoryM(CategoryM categoryM) {
        this.categoryM = categoryM;
    }

    public void setCategoryS(CategoryS categoryS) {
        this.categoryS = categoryS;
    }

    public String getHashtag() {
        return hashtag;
    }


}
