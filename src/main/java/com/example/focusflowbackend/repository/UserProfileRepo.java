package com.example.focusflowbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.focusflowbackend.models.UserProfile;

public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

}
