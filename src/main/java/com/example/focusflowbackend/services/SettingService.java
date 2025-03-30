package com.example.focusflowbackend.services;

import com.example.focusflowbackend.models.Setting;
import com.example.focusflowbackend.repository.SettingRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SettingService {

    private final SettingRepo settingRepository;

    public SettingService(SettingRepo settingRepository) {
        this.settingRepository = settingRepository;
    }

    // get by userId
    public Setting getSettingByUserId(Long userId) {
        return settingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));
    }

    // update by userId
    public Setting updateSetting(Long userId, Setting updatedSetting) {
        Setting existingSetting = settingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));

        existingSetting.setLanguage(updatedSetting.getLanguage());
        existingSetting.setTheme(updatedSetting.getTheme());
        existingSetting.setTaskReminder(updatedSetting.getTaskReminder());
        existingSetting.setNotificationEnabled(updatedSetting.getNotificationEnabled());
        existingSetting.setPomodoroDuration(updatedSetting.getPomodoroDuration());
        existingSetting.setShortBreak(updatedSetting.getShortBreak());
        existingSetting.setLongBreak(updatedSetting.getLongBreak());
        existingSetting.setPomodoroRounds(updatedSetting.getPomodoroRounds());
        existingSetting.setTimezone(updatedSetting.getTimezone());

        return settingRepository.save(existingSetting);
    }
}
