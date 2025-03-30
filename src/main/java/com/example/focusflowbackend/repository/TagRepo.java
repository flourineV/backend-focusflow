package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepo extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name); // Tìm theo tên tag
}
