package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Pomodoro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PomodoroRepo extends JpaRepository<Pomodoro, Long> {

    List<Pomodoro> findByUserId(Long userId);

    List<Pomodoro> findByTaskId(Long taskId);

    List<Pomodoro> findByUserIdAndTaskId(Long userId, Long taskId);
}
