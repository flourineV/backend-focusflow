package com.example.focusflowbackend.dto.streakrecord;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StreakRecordDTO {

    // ✅ DTO để tạo streak mới cho user
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        private Long userId;
    }

    // ✅ DTO để trả về response
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private Long userId;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer streakLength;
        private Integer longestStreak;
        private LocalDateTime createdAt;
    }
}
