package com.example.focusflowbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "streak_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreakRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "streak_length", nullable = false)
    private Integer streakLength;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
