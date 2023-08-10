package com.example.vblogserver.domain.user.service;

import com.example.vblogserver.domain.user.entity.RefreshToken;
import com.example.vblogserver.domain.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    public Optional<RefreshToken> findByUserId(Long id) {
        return refreshTokenRepository.findByUserId(id);
    }
}
