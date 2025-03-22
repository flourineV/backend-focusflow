package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepo extends JpaRepository<ProjectTask, Long> {

    // Tìm tất cả ProjectTask theo project_id
    List<ProjectTask> findByProjectId(Long projectId);

    // Tìm tất cả ProjectTask theo task_id
    List<ProjectTask> findByTaskId(Long taskId);
}
