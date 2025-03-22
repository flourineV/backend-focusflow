package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {

    // Tìm tất cả Project theo user_id
    List<Project> findByUser_Id(Long userId);
}
