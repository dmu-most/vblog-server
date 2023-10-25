package com.example.vblogserver.domain.user.repository;

import com.example.vblogserver.domain.user.entity.Option;
import com.example.vblogserver.domain.user.entity.OptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    Optional<Option> findByType(OptionType type);
}
