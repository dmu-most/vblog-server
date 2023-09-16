package com.example.vblogserver.domain.bookmark.entity;

import com.example.vblogserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    //찜 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    //찜 여부 (true, false)
    private Boolean bookmark;
    //게시글 ID
    private Long contentId;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    //찜한 유저
    private User user;

    public Bookmark(Boolean bookmark, Long contentId, User user) {
        this.bookmark = bookmark;
        this.contentId = contentId;
        this.user = user;
    }
}
