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

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    private String writer;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    private String title;
    private String link;
    private String description;
    private String thumbnails;
    private String hashtag;

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private String createdDate;

    @ColumnDefault("0")
    private Integer reviewCount;
    @ColumnDefault("0")
    private Integer likeCount;
    @ColumnDefault("0")
    private Integer disLikeCount;
    private Float grade;

    @ManyToOne
    @JoinColumn(name = "category_g")
    private CategoryG categoryG;
    @ManyToOne
    @JoinColumn(name = "category_m")
    private CategoryM categoryM;
    @ManyToOne
    @JoinColumn(name = "category_s")
    private CategoryS categoryS;


    @Builder
    public Board(String title, String link, String description, User user) {
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
