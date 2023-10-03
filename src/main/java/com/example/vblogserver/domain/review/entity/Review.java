package com.example.vblogserver.domain.review.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    private String content;
    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    //게시글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 리뷰 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loginId")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // 평점
    private float grade;

    public void setGrade(float grade) {
        this.grade = grade;
    }

    @Builder
    public Review(String content, Board board,  User user, float grade) {
        this.content = content;
        this.board = board;
        this.user = user;
        this.grade = grade;
    }

    //사용자가 별도 작성 시간 입력 없이 댓글 작성이 완료되면 시간을 서버의 시간으로 insert
    @PrePersist
    public void prePersist(){
        createdDate = LocalDateTime.now();
    }

    public void setContent(String newContent) {
        this.content = newContent;
    }
}