package com.example.vblogserver.domain.bookmark.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // vlog or blog
    private String type;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(mappedBy = "folder")
    private List<Board> boards = new ArrayList<>();
}

