package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubtaskRepo extends JpaRepository<Subtask, Long> {

    List<Subtask> findByTaskId(Long taskId);
}
