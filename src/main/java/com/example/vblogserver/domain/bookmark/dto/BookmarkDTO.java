package com.example.vblogserver.domain.bookmark.dto;

import com.example.vblogserver.domain.bookmark.entity.Bookmark;

public class BookmarkDTO {
	private Long bookmarkId; // 찜 ID
	private Long boardId; // 게시글 ID

	public BookmarkDTO(Long bookmarkId, Long boardId) {
		this.bookmarkId = bookmarkId;
		this.boardId = boardId;
	}

	public Long getBookmarkId() {
		return bookmarkId;
	}

	public void setBookmarkId(Long bookmarkId) {
		this.bookmarkId = bookmarkId;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}
}
