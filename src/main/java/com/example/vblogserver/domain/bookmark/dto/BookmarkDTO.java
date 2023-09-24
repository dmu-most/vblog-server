package com.example.vblogserver.domain.bookmark.dto;

import com.example.vblogserver.domain.bookmark.entity.Bookmark;

public class BookmarkDTO {
	private Long id; // 찜 ID
	private Boolean bookmark; // 찜 여부
	private Long boardId; // 게시글 ID

	public BookmarkDTO(Bookmark bookmark) {
		this.id = bookmark.getId();
		this.bookmark = bookmark.getBookmark();
		this.boardId = bookmark.getBoard().getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getBookmark() {
		return bookmark;
	}

	public void setBookmark(Boolean bookmark) {
		this.bookmark = bookmark;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}
}
