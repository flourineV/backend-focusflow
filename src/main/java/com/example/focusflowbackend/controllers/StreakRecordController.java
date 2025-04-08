package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.streakrecord.StreakRecordDTO; // Sửa tên DTO cho đúng
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.services.StreakRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/streak-records")
@RequiredArgsConstructor
public class StreakRecordController {

    private final StreakRecordService streakRecordService;
    private final AuthorizationUtils authUtils;

    // Get streak records by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StreakRecordDTO.Response>> getStreakRecordsByUserId(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return authUtils.createForbiddenResponse();
            }

            List<StreakRecordDTO.Response> responses = streakRecordService.getStreakRecordsByUserId(userId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Create new streak (if not exist streaks)
    @PostMapping("/user/{userId}/create")
    public ResponseEntity<String> createStreakForNewUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
            }

            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body("Invalid user ID");
            }
            streakRecordService.createStreakForNewUser(userId);
            return ResponseEntity.ok("Streak created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    // Update streak (+1)
    @PutMapping("/user/{userId}/update")
    public ResponseEntity<String> updateStreak(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
            }

            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body("Invalid user ID");
            }
            streakRecordService.updateStreak(userId);
            return ResponseEntity.ok("Streak updated");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    // Update longest streak
    @PutMapping("/user/{userId}/update-longest")
    public ResponseEntity<String> updateLongestStreak(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
            }

            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body("Invalid user ID");
            }
            streakRecordService.updateLongestStreak(userId);
            return ResponseEntity.ok("Longest streak updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    //Check miss
    @GetMapping("/user/{userId}/check-miss")
    public ResponseEntity<String> checkStreakMiss(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
        }

        boolean missed = streakRecordService.hasMissedStreak(userId);
        return ResponseEntity.ok(missed ? "missed" : "continuous");
    }

    //Reset if miss
    @PostMapping("/user/{userId}/reset-streak")
    public ResponseEntity<String> resetStreak(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
            }

            streakRecordService.createStreakForNewUser(userId);
            return ResponseEntity.ok("New streak created after miss");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error resetting streak");
        }
    }

    // Check the condition to update streak
    @PutMapping("/user/{userId}/check-today")
    public ResponseEntity<String> checkTodayStreak(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
            }

            boolean logged = streakRecordService.checkAndLogTodayStreak(userId);
            if (logged) {
                return ResponseEntity.ok("Streak logged for today");
            } else {
                return ResponseEntity.ok("Not enough activity today, no streak log created");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error while checking today's streak");
        }
    }
}
