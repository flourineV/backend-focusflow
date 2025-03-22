package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

    // Tìm tất cả Task theo user_id
    List<Task> findByUser_Id(Long userId);
}
