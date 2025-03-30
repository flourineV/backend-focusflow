package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.PomodoroSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PomodoroSessionRepo extends JpaRepository<PomodoroSession, Long> {

    List<PomodoroSession> findByPomodoroId(Long pomoId);

}
