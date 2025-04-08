package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.RefreshToken;
import com.example.focusflowbackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    int deleteByUser(User user);
    
    Optional<RefreshToken> findByUser(User user);
} 