package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Task_Tags;
import com.example.focusflowbackend.models.Task;
import com.example.focusflowbackend.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskTagRepo extends JpaRepository<Task_Tags, Long> {

    List<Task_Tags> findByTask(Task task); // Lấy tag theo task

    List<Task_Tags> findByTag(Tag tag);    // Lấy task theo tag
}
