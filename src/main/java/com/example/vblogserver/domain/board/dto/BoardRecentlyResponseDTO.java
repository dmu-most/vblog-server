package com.example.vblogserver.domain.board.dto;

import com.example.vblogserver.domain.board.entity.Board;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BoardRecentlyResponseDTO {
    private Long id;
    private String writer;
    private String title;
    private String link;
    private String description;
    private String thumbnails;
    private String hashtag;
    private LocalDate createdDate;

    public BoardRecentlyResponseDTO(Board board) {
        this.id = board.getId();
        this.writer = board.getWriter();
        this.title = board.getTitle();
        this.link = board.getLink();
        this.description = board.getDescription();
        this.thumbnails = board.getThumbnails();
        this.hashtag = board.getHashtag();
        this.createdDate = board.getCreatedDate();
    }
}

