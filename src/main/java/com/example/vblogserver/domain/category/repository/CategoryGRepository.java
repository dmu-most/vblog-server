package com.example.vblogserver.domain.category.repository;

import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryGRepository extends JpaRepository<CategoryG, Long> {

}
