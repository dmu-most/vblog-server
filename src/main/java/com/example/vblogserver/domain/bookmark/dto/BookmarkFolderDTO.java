package com.example.vblogserver.domain.bookmark.dto;

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
}
