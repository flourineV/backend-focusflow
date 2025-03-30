package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepo extends JpaRepository<Project, Long> {

    List<Project> findByUserId(Long userId);
}
