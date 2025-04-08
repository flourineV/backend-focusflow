package com.example.focusflowbackend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDTO {

    // Thống kê người dùng
    private long totalUsers;
    private long newUsersLast7Days;

    // Thống kê nhiệm vụ
    private long totalTasks;
    private long completedTasks;

    // Thống kê Pomodoro
    private long totalPomodoros;
    private long totalFocusTimeMinutes;

    // Thống kê DAU trung bình
    private double averageDAULast7Days;
    private double averageDAULast30Days;
    private double averageDAULast365Days;

    // Tỷ lệ hoàn thành nhiệm vụ
    public double getTaskCompletionRate() {
        return totalTasks > 0 ? (double) completedTasks / totalTasks : 0;
    }

    // Thời gian tập trung trung bình trên mỗi Pomodoro (phút)
    public double getAverageFocusTimePerPomodoro() {
        return totalPomodoros > 0 ? (double) totalFocusTimeMinutes / totalPomodoros : 0;
    }
}
