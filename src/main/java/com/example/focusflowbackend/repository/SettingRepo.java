package com.example.focusflowbackend.repository;

import com.example.focusflowbackend.models.Setting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingRepo extends JpaRepository<Setting, Long> {

    Optional<Setting> findByUserId(Long userId);
}
