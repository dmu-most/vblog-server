package com.example.vblogserver.domain.board.dto;

import com.example.vblogserver.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BoardDTO {
    private Long id;
    private String writer;
    private String title;
    private String link;
    private String description;
    private LocalDate createdDate;
    private String hashtag;

    public BoardDTO(Board board) {
        this.id = board.getId();
        this.writer = board.getWriter();
        this.title = board.getTitle();
        this.link = board.getLink();
        this.description = board.getDescription();
        this.createdDate = board.getCreatedDate();
        this.hashtag = board.getHashtag();
    }
}
