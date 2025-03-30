package com.example.focusflowbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "setting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    private String language;
    private String theme;

    @Column(name = "task_reminder")
    private Boolean taskReminder;

    @Column(name = "notification_enabled")
    private Boolean notificationEnabled;

    @Column(name = "pomodoro_duration")
    private Integer pomodoroDuration;

    @Column(name = "short_break")
    private Integer shortBreak;

    @Column(name = "long_break")
    private Integer longBreak;

    @Column(name = "pomodoro_rounds")
    private Integer pomodoroRounds;

    private String timezone;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
