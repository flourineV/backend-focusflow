package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Pomodoro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PomodoroRepo extends JpaRepository<Pomodoro, Long> {

    List<Pomodoro> findByUserId(Long userId);

    List<Pomodoro> findByTaskId(Long taskId);

    List<Pomodoro> findByUserIdAndTaskId(Long userId, Long taskId);

    @Query(value = "SELECT * FROM pomodoro p WHERE p.user_id = :userId AND DATE(p.end_time) = :date AND p.is_deleted = false", nativeQuery = true)
    List<Pomodoro> findPomodorosByUserAndDate(@Param("userId") Long userId,
            @Param("date") LocalDate date);

    @Query(value = "SELECT COALESCE(SUM(p.total_time), 0) FROM pomodoro p WHERE p.user_id = :userId AND DATE(p.end_time) = :date AND p.is_deleted = false", nativeQuery = true)
    int getTotalPomodoroTimeByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

}
