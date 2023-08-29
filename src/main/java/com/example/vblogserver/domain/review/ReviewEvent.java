package com.example.vblogserver.domain.review;

public class ReviewEvent {
    private Long boardId;

    public ReviewEvent(Long boardId) {
        this.boardId = boardId;
    }

    public Long getBoardId() {
        return boardId;
    }
}
