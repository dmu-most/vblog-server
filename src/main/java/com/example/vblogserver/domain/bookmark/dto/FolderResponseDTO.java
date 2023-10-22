package com.example.vblogserver.domain.bookmark.dto;

import com.example.vblogserver.domain.board.entity.Board;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FolderResponseDTO {
    private Long id;
    private String name;
    private String type;
    private Long userId;
    private List<Board> boards;
}
