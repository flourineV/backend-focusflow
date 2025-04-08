package com.example.focusflowbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime dueDate;

    private Integer priority;

    private boolean isCompleted;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String repeatStyle;

    private boolean isDeleted;

    @Column(columnDefinition = "TEXT")
    private String note;

    private Integer reminderDaysBefore;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subtask> subtasks;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Nếu isCompleted chuyển từ false sang true thì cập nhật completedAt
        if (this.isCompleted && (this.completedAt == null)) {
            this.completedAt = LocalDateTime.now();
        }

        // Nếu isCompleted chuyển từ true sang false thì xóa completedAt
        if (!this.isCompleted && (this.completedAt != null)) {
            this.completedAt = null;
        }
    }
}
