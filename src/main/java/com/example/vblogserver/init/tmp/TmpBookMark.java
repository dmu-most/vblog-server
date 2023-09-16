package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TmpBookMark {
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    public TmpBookMark(BoardService boardService, UserRepository userRepository, BookmarkRepository bookmarkRepository) {
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    //테스트 계정에 찜 항목 추가
    public void updateTmpBookMark(){
        for(int i=1; i<361; i+=50){
            Long int_to_long = (long) i;
            Board board = boardService.getBoardById(int_to_long);

            if(board == null) {
                System.out.println("찜 저장 실패. 게시글이 존재하지 않음");
                return;
            }
            Boolean bookmark = true;
            String userId = "testuser";

            User user;
            try {
                user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다(찜)"));
            } catch (IllegalArgumentException e) {
                System.out.println("userID를 찾을 수 없습니다.(찜)");
                return ;
            }

            Bookmark saveBookmark = new Bookmark(bookmark, board.getId(), user);
            bookmarkRepository.save(saveBookmark);
        }
        System.out.println("찜 저장 성공");
        return ;
    }


}
