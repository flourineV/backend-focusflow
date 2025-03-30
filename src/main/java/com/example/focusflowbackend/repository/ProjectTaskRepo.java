package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Project_Task;
import com.example.focusflowbackend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTaskRepo extends JpaRepository<Project_Task, Long> {

    List<Project_Task> findByProjectId(Long projectId);

    List<Project_Task> findByTaskId(Long taskId);

    List<Project_Task> findByTask(Task task);

    void deleteByTaskId(Long taskId);
}
