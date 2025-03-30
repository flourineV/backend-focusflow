package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreakRepo extends JpaRepository<Streak, Long> {

    Optional<Streak> findByUserId(Long userId);
}
