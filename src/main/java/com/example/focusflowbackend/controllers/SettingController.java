package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.models.Setting;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @Autowired
    private AuthorizationUtils authUtils;

    // GET settings
    @GetMapping("/{userId}")
    public ResponseEntity<Setting> getSettingsByUserId(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        Setting setting = settingService.getSettingByUserId(userId);
        return ResponseEntity.ok(setting);
    }

    // Update settings
    @PutMapping("/{userId}")
    public ResponseEntity<Setting> updateSettings(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestBody Setting updatedSetting) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        Setting setting = settingService.updateSetting(userId, updatedSetting);
        return ResponseEntity.ok(setting);
    }
}
