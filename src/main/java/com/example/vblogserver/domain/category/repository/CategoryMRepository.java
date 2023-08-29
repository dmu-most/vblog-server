package com.example.vblogserver.domain.category.repository;

import com.example.vblogserver.domain.category.entity.CategoryM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMRepository extends JpaRepository<CategoryM, Long> {
}
