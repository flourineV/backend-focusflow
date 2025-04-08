package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.PomodoroSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PomodoroSessionRepo extends JpaRepository<PomodoroSession, Long> {
// ✅ Lấy danh sách sessions theo Pomodoro ID

    List<PomodoroSession> findByPomodoroId(Long pomodoroId);

    // ✅ Lấy danh sách sessions theo User ID (thông qua Pomodoro)
    @Query("SELECT ps FROM PomodoroSession ps WHERE ps.pomodoro.user.id = :userId")
    List<PomodoroSession> findByPomodoroUserId(@Param("userId") Long userId);
}
