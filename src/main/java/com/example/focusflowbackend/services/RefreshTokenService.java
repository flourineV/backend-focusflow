package com.example.focusflowbackend.services;

import com.example.focusflowbackend.models.RefreshToken;
import com.example.focusflowbackend.models.User;
import com.example.focusflowbackend.repository.RefreshTokenRepo;
import com.example.focusflowbackend.repository.UserAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserAccountRepo userRepo;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Xóa refresh token cũ nếu có
        refreshTokenRepo.findByUser(user).ifPresent(token -> refreshTokenRepo.deleteByUser(user));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepo.save(refreshToken);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepo.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new sign in request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return refreshTokenRepo.deleteByUser(user);
    }
} 