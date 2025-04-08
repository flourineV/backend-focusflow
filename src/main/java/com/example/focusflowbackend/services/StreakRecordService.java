package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.streakrecord.StreakRecordDTO;
import com.example.focusflowbackend.models.StreakRecord;
import com.example.focusflowbackend.models.User;
import com.example.focusflowbackend.repository.StreakRecordRepo;
import com.example.focusflowbackend.repository.UserAccountRepo;
import com.example.focusflowbackend.repository.TaskRepo;
import com.example.focusflowbackend.repository.PomodoroRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StreakRecordService {

    private final StreakRecordRepo streakRecordRepo;
    private final UserAccountRepo userRepo;
    private final TaskRepo taskRepo;
    private final PomodoroRepo pomodoroRepo;

    @Transactional
    public void createStreakForNewUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StreakRecord newStreak = StreakRecord.builder()
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .streakLength(0)
                .longestStreak(0)
                .createdAt(LocalDateTime.now())
                .build();
        streakRecordRepo.save(newStreak);
    }

    public List<StreakRecordDTO.Response> getStreakRecordsByUserId(Long userId) {
        return streakRecordRepo.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private StreakRecordDTO.Response convertToDto(StreakRecord record) {
        return StreakRecordDTO.Response.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .startDate(record.getStartDate())
                .endDate(record.getEndDate())
                .streakLength(record.getStreakLength())
                .longestStreak(record.getLongestStreak())
                .createdAt(record.getCreatedAt())
                .build();
    }

    public boolean checkStreakConditions(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        long completedTasks = taskRepo.countCompletedToday(userId, startOfDay, endOfDay);
        int totalPomodoroMinutes = pomodoroRepo.getTotalPomodoroTimeByUserAndDate(userId, date);

        return completedTasks >= 3 && totalPomodoroMinutes >= 30;
    }

    public boolean hasMissedStreak(Long userId) {
        Optional<StreakRecord> lastStreakOpt = streakRecordRepo.findTopByUserIdOrderByEndDateDesc(userId);
        if (lastStreakOpt.isEmpty()) {
            return true;
        }

        LocalDate today = LocalDate.now();
        return !lastStreakOpt.get().getEndDate().isEqual(today.minusDays(1));
    }

    @Transactional
    public boolean checkAndLogTodayStreak(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long completedTasks = taskRepo.countCompletedToday(userId, startOfDay, endOfDay);
        int totalPomodoroMinutes = pomodoroRepo.getTotalPomodoroTimeByUserAndDate(userId, today);

        if (completedTasks < 3 || totalPomodoroMinutes < 30) {
            return false;
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<StreakRecord> lastStreakOpt = streakRecordRepo.findTopByUserIdOrderByEndDateDesc(userId);
        int newStreakLength = 1;

        if (lastStreakOpt.isPresent() && lastStreakOpt.get().getEndDate().isEqual(today.minusDays(1))) {
            newStreakLength = lastStreakOpt.get().getStreakLength() + 1;
        }

        StreakRecord newRecord = StreakRecord.builder()
                .user(user)
                .startDate(today)
                .endDate(today)
                .streakLength(newStreakLength)
                .longestStreak(newStreakLength)
                .createdAt(LocalDateTime.now())
                .build();

        streakRecordRepo.save(newRecord);
        return true;
    }

    @Transactional
    public void updateLongestStreak(Long userId) {
        Optional<StreakRecord> lastStreakOpt = streakRecordRepo.findTopByUserIdOrderByEndDateDesc(userId);
        if (lastStreakOpt.isEmpty()) {
            return;
        }

        StreakRecord last = lastStreakOpt.get();
        if (last.getStreakLength() > last.getLongestStreak()) {
            last.setLongestStreak(last.getStreakLength());
            streakRecordRepo.save(last);
        }
    }

    @Transactional
    public void resetStreak(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StreakRecord newRecord = StreakRecord.builder()
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .streakLength(0)
                .longestStreak(0)
                .createdAt(LocalDateTime.now())
                .build();

        streakRecordRepo.save(newRecord);
    }

    @Transactional
    public void updateStreak(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<StreakRecord> lastStreakOpt = streakRecordRepo.findTopByUserIdOrderByEndDateDesc(userId);

        if (lastStreakOpt.isEmpty()) {
            throw new RuntimeException("User has no existing streak record");
        }

        StreakRecord lastStreak = lastStreakOpt.get();

        if (lastStreak.getEndDate().isEqual(today.minusDays(1))) {
            lastStreak.setEndDate(today);
            lastStreak.setStreakLength(lastStreak.getStreakLength() + 1);
            streakRecordRepo.save(lastStreak);
        } else {
            throw new RuntimeException("User missed a day, streak was reset");
        }
    }

}
