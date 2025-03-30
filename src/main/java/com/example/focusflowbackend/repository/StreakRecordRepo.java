package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.StreakRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StreakRecordRepo extends JpaRepository<StreakRecord, Long> {

    List<StreakRecord> findByUserId(Long userId);
}
