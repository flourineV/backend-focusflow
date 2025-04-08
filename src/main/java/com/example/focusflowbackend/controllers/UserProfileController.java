package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.models.UserProfile;
import com.example.focusflowbackend.services.UserProfileService;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final AuthorizationUtils authUtils;

    public UserProfileController(UserProfileService userProfileService, AuthorizationUtils authUtils) {
        this.userProfileService = userProfileService;
        this.authUtils = authUtils;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ UserProfileController đã được Spring Boot load thành công!");
    }

    // GET user by user_id
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            System.out.println("Called userProfileService.getProfileByUserId...");
            UserProfile userProfile = userProfileService.getProfileByUserId(userId);

            System.out.println("Successful query: " + userProfile);
            return new ResponseEntity<>(userProfile, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Error while query user profile: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update userProfile
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfile updatedProfile, @RequestHeader("Authorization") String token) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            UserProfile updatedUserProfile = userProfileService.updateProfile(userId, updatedProfile);
            return new ResponseEntity<>(updatedUserProfile, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Error in updateUserProfile: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // (expected) Update last active
    @PutMapping("/{userId}/last-active")
    public ResponseEntity<Void> updateLastActiveTime(
            @PathVariable Long userId,
            @RequestParam String fcmToken,
            @RequestHeader("Authorization") String token) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            userProfileService.updateLastActiveTime(userId, fcmToken);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.out.println("Error in updateLastActiveTime: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // (expected) handle FCM token
    @PostMapping("/{userId}/shutdown")
    public ResponseEntity<Void> handleAppShutdown(
            @PathVariable Long userId,
            @RequestParam String fcmToken,
            @RequestHeader("Authorization") String token) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            userProfileService.handleAppShutdown(userId, fcmToken);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.out.println("Error in handleAppShutdown: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
