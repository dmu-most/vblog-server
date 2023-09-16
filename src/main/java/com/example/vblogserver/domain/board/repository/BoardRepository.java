package com.example.vblogserver.domain.board.repository;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllBy();
    List<Board> findByCategoryG(CategoryG categoryG);

    List<Board> findByCategoryGAndCategoryM(CategoryG categoryG, CategoryM categoryM);

    //베너 조회 용. 좋아요가 가장 높은 데이터 검색
    Optional<Board> findTopByCategoryGOrderByLikeCountDesc(CategoryG categoryG);
    //메인페이지 조회용. 좋아요 순으로 데이터 검색
    List<Board> findByCategoryGOrderByLikeCountDesc(CategoryG categoryG);
    //메인페이지 조회용. 최신 순으로 데이터 검색
    List<Board> findByCategoryGOrderByCreatedDateDesc(CategoryG categoryG);
    //메인페이지 조회용. 리뷰가 가장 많은 순으로 검색
    List<Board> findByCategoryGOrderByReviewCountDesc(CategoryG categoryG);

}
