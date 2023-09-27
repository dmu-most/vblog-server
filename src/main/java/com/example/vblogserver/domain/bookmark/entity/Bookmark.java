package com.example.vblogserver.domain.bookmark.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    //찜 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 게시글 ID - 수정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id")
    private Board board;

    //찜한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folderId")
    private BookmarkFolder bookmarkFolder;

    @Builder
    public Bookmark(Board board, User user, BookmarkFolder bookmarkFolder) {
        //this.bookmark = bookmark;
        this.board = board;
        this.user = user;
        this.bookmarkFolder = bookmarkFolder;
    }

    public void setBookmarkFolder(BookmarkFolder bookmarkFolder) {
        this.bookmarkFolder = bookmarkFolder;
    }
}
