package com.example.vblogserver.domain.bookmark.repository;

import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

}
