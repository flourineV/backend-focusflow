package com.example.focusflowbackend.models;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "task_id", nullable = true)
    private Task task;

    @Column(name = "focus_time")
    private Integer focusTime;

    @Column(name = "break_time")
    private Integer breakTime;

    @Column(name = "total_time")
    private Integer totalTime;

    private String note;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "pomodoro", cascade = CascadeType.ALL)
    private List<PomodoroSession> sessions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
