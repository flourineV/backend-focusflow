package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.StreakRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StreakRecordRepo extends JpaRepository<StreakRecord, Long> {

    List<StreakRecord> findByUserId(Long userId);

    // ✅ Lấy streak mới nhất của user (dựa trên endDate)
    Optional<StreakRecord> findTopByUserIdOrderByEndDateDesc(Long userId);
}
