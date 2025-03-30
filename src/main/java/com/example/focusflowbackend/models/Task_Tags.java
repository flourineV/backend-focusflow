package com.example.focusflowbackend.models;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "task_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task_Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore // Thêm @JsonIgnore để tránh vòng lặp vô hạn khi chuyển đổi sang JSON
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    @JsonIgnore // Thêm @JsonIgnore để tránh vòng lặp vô hạn khi chuyển đổi sang JSON
    private Tag tag;
}
