package com.example.vblogserver.domain.bookmark.dto;

import com.example.vblogserver.domain.bookmark.entity.BookmarkFolder;

public class BookmarkFolderDTO {
	private Long id;
	private String name;

	public BookmarkFolderDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BookmarkFolderDTO(BookmarkFolder folder) {
		this.name = folder.getName();
		//this.user = folder.getUser();
		//this.bookmarks = folder.getBookmarks();
	}
}
