package com.example.focusflowbackend.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user; // Liên kết với bảng User

    @Column(nullable = false, unique = true)
    private String username; // Tên người dùng duy nhất

    private String avatar;
    private String bio;
    private String phone;

    @Column(name = "birthdate")
    private LocalDate birthdate; // Ngày sinh

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; // Giới tính (Enum: MALE, FEMALE, OTHER)

    private String country;

    @Column(name = "last_login")
    private LocalDateTime lastLogin; // Lần đăng nhập cuối

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Ngày tạo

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Ngày cập nhật gần nhất

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
