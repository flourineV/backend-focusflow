package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    @Query("SELECT t FROM Task t JOIN Project_Task pt ON pt.task.id = t.id WHERE pt.project.id = :projectId")
    List<Task> findTasksByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.isCompleted = true AND t.completedAt BETWEEN :start AND :end")
    long countCompletedToday(@Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
