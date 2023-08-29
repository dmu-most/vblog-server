package com.example.vblogserver.domain.board.repository;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByCategoryG(CategoryG categoryG);
}
