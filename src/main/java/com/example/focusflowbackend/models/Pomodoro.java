package com.example.focusflowbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pomodoro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pomodoro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "session_date")
    private LocalDate sessionDate;

    @Column(name = "focus_time")
    private Integer focusTime;

    @Column(name = "break_time")
    private Integer breakTime;

    @Column(name = "total_time")
    private Integer totalTime;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "pomodoro", cascade = CascadeType.ALL)
    private List<PomodoroSession> sessions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
