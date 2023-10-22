package com.example.vblogserver.domain.bookmark.dto;

import com.example.vblogserver.domain.board.dto.BoardDTO;
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
    private List<BoardResponseDTO> boards;

    public List<BoardResponseDTO> getBoards() {
        return boards;
    }

    public void setBoards(List<BoardResponseDTO> boards) {
        this.boards = boards;
    }
}
