package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.models.Setting;
import com.example.focusflowbackend.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;

    // GET /api/settings/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<Setting> getSettingsByUserId(@PathVariable Long userId) {
        Setting setting = settingService.getSettingByUserId(userId);
        return ResponseEntity.ok(setting);
    }

    // PUT /api/settings/{userId}
    @PutMapping("/{userId}")
    public ResponseEntity<Setting> updateSettings(@PathVariable Long userId, @RequestBody Setting updatedSetting) {
        Setting setting = settingService.updateSetting(userId, updatedSetting);
        return ResponseEntity.ok(setting);
    }
}
