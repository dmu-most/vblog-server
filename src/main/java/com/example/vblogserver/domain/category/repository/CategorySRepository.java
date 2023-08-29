package com.example.vblogserver.domain.category.repository;

import com.example.vblogserver.domain.category.entity.CategoryS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorySRepository extends JpaRepository<CategoryS, Long> {
}
