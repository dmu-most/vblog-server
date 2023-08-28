package com.example.vblogserver.domain.user.repository;

import com.example.vblogserver.domain.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long id);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
