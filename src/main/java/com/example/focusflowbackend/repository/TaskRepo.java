package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    @Query("SELECT t FROM Task t JOIN Project_Task pt ON pt.task.id = t.id WHERE pt.project.id = :projectId")
    List<Task> findTasksByProjectId(@Param("projectId") Long projectId);

}
