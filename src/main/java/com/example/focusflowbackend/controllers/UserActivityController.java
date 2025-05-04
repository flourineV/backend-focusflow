package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.services.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserActivityController {

    @Autowired
    private UserActivityService activityService;

    @PostMapping("/openapp")
    public ResponseEntity<?> userOpenedApp(@RequestParam Long userId) {
        activityService.trackUser(userId);
        return ResponseEntity.ok("✅ Đã ghi nhận user online");
    }
}
