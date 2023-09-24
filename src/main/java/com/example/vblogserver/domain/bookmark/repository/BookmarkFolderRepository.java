package com.example.vblogserver.domain.bookmark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vblogserver.domain.bookmark.entity.BookmarkFolder;
import com.example.vblogserver.domain.user.entity.User;

public interface BookmarkFolderRepository extends JpaRepository<BookmarkFolder, Long> {
	List<BookmarkFolder> findByUserOrderByCreatedDateDesc(User user);
	List<BookmarkFolder> findByUser(User user);

}
