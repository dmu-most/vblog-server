package com.example.vblogserver.domain.LikeInfo.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeInfo {

    // 좋아요, 싫어요 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LikeId", updatable = false)
    private Long LikeId;

    // true 일 경우 좋아요, false 일 경우 싫어요
    private boolean likeInfo;

    // 좋아요, 싫어요 클릭한 게시글 ID
    @ManyToOne
    @JoinColumn(name = "boardID")
    private Board board;

    // 좋아요, 싫어요 클릭한 사용자 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    private User user;

    @Builder
    public LikeInfo(boolean likeInfo, Board board, User user) {
        this.likeInfo = likeInfo;
        this.board = board;
        this.user = user;
    }

    public void setLikeInfo(boolean likeInfo) {
        this.likeInfo = likeInfo;
    }

    public boolean getLikeInfo() {
        return likeInfo;
    }


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
