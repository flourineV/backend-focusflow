package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.exception.ForbiddenException;
import com.example.focusflowbackend.exception.ResourceNotFoundException;
import com.example.focusflowbackend.models.UserProfile;
import com.example.focusflowbackend.services.UserProfileService;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;
    private final AuthorizationUtils authUtils;

    public UserProfileController(UserProfileService userProfileService, AuthorizationUtils authUtils) {
        this.userProfileService = userProfileService;
        this.authUtils = authUtils;
    }

    @PostConstruct
    public void init() {
        logger.info("✅ UserProfileController đã được Spring Boot load thành công!");
    }

    // GET user by user_id
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            throw new ForbiddenException("Access denied: You do not have permission to access this resource");
        }

        logger.debug("Called userProfileService.getProfileByUserId...");
        UserProfile userProfile = userProfileService.getProfileByUserId(userId);

        if (userProfile == null) {
            throw new ResourceNotFoundException("User profile not found with id: " + userId);
        }

        logger.debug("Successful query: {}", userProfile);
        return ResponseEntity.ok(userProfile);
    }

    // Update userProfile
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfile updatedProfile, @RequestHeader("Authorization") String token) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            throw new ForbiddenException("Access denied: You do not have permission to access this resource");
        }

        try {
            UserProfile updatedUserProfile = userProfileService.updateProfile(userId, updatedProfile);
            return ResponseEntity.ok(updatedUserProfile);
        } catch (Exception e) {
            logger.error("Error in updateUserProfile: {}", e.getMessage());
            throw new ResourceNotFoundException("User profile not found with id: " + userId);
        }
    }

    // (expected) Update last active
    @PutMapping("/{userId}/last-active")
    public ResponseEntity<Void> updateLastActiveTime(
            @PathVariable Long userId,
            @RequestParam String fcmToken,
            @RequestHeader("Authorization") String token) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            throw new ForbiddenException("Access denied: You do not have permission to access this resource");
        }

        try {
            userProfileService.updateLastActiveTime(userId, fcmToken);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error in updateLastActiveTime: {}", e.getMessage());
            throw new ResourceNotFoundException("User profile not found with id: " + userId);
        }
    }

    // (expected) handle FCM token
    @PostMapping("/{userId}/shutdown")
    public ResponseEntity<Void> handleAppShutdown(
            @PathVariable Long userId,
            @RequestParam String fcmToken,
            @RequestHeader("Authorization") String token) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            throw new ForbiddenException("Access denied: You do not have permission to access this resource");
        }

        try {
            userProfileService.handleAppShutdown(userId, fcmToken);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error in handleAppShutdown: {}", e.getMessage());
            throw new ResourceNotFoundException("User profile not found with id: " + userId);
        }
    }
}
