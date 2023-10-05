package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.entity.BookmarkFolder;
import com.example.vblogserver.domain.bookmark.repository.BookmarkFolderRepository;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TmpBookMark {
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkFolderRepository bookmarkFolderRepository;

    public TmpBookMark(BoardService boardService, UserRepository userRepository, BookmarkRepository bookmarkRepository, BookmarkFolderRepository bookmarkFolderRepository) {
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.bookmarkFolderRepository = bookmarkFolderRepository;
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

            String userId = "testuser";

            User user;
            try {
                user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다(찜)"));
            } catch (IllegalArgumentException e) {
                System.out.println("userID를 찾을 수 없습니다.(찜)");
                return ;
            }
            String folderName = "재밌는 영상";
            BookmarkFolder bookmarkFolder = new BookmarkFolder(folderName, user, new ArrayList<>());

            List<BookmarkFolder> existingFolders = bookmarkFolderRepository.findByNameAndUser(folderName, user);

            if (!existingFolders.isEmpty()) {
                // 폴더가 이미 존재하면, 첫 번째 폴더를 사용합니다.
                bookmarkFolder = existingFolders.get(0);
            } else {
                // 폴더가 존재하지 않으면 새로 만들고 저장합니다.
                bookmarkFolder = bookmarkFolderRepository.save(bookmarkFolder);
            }
            Bookmark saveBookmark = Bookmark.builder()
                    .board(board)
                    .user(user)
                    .bookmarkFolder(bookmarkFolder)
                    .build();
            bookmarkRepository.save(saveBookmark);

        }
        System.out.println("찜 저장 성공");
    }


}
