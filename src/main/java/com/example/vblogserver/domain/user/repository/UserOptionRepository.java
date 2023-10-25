package com.example.vblogserver.domain.user.repository;

import com.example.vblogserver.domain.user.entity.UserOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOptionRepository extends JpaRepository<UserOption, Long> {
    List<UserOption> findByUserId(Long userId);
}
