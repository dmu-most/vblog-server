package com.example.vblogserver.domain.bookmark.repository;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Bookmark findByBoardAndFolderAndUser(Board board, Folder folder, User user);

    List<Bookmark> findByFolder(Folder folder);
}
