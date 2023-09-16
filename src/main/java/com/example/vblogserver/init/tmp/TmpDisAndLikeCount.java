package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import org.springframework.stereotype.Component;

@Component
public class TmpDisAndLikeCount {
    private final BoardRepository boardRepository;

    public TmpDisAndLikeCount(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }


    public void updateBoardDisAndLikeCount() {
        for(int i = 1; i<361; i++) {
            Long intToLong = (long) i;
            Board board = boardRepository.findById(intToLong).orElse(null);
            board.setLikeCount(i);
            board.setDisLikeCount(362-i);
            boardRepository.save(board);
        }

    }
}
