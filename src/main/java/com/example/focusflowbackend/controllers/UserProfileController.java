package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.models.UserProfile;
import com.example.focusflowbackend.services.UserProfileService;

import jakarta.annotation.PostConstruct;

import com.example.focusflowbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserProfileController(UserProfileService userProfileService, JwtUtil jwtUtil) {
        this.userProfileService = userProfileService;
        this.jwtUtil = jwtUtil;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ UserProfileController đã được Spring Boot load thành công!");
    }

    // Lấy hồ sơ user theo user_id
    @SuppressWarnings("null")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        System.out.println("🚀 [UserProfileController] API getUserProfile được gọi với userId: " + userId);
        System.out.println("🔍 Bắt đầu xử lý API...");

        try {
            System.out.println("🔄 Gọi userProfileService.getProfileByUserId...");
            UserProfile userProfile = userProfileService.getProfileByUserId(userId);

            System.out.println("✅ Truy vấn thành công, kết quả: " + userProfile);
            return new ResponseEntity<>(userProfile, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("❌ Lỗi khi truy vấn user profile: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Cập nhật hồ sơ user
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfile updatedProfile, @RequestHeader("Authorization") String token) {
        try {
            // Loại bỏ khoảng trắng thừa từ token
            token = token.trim();

            System.out.println("Received userId: " + userId);
            System.out.println("Received token: " + token); // Log token để kiểm tra

            // Lấy thông tin người dùng từ JWT
            Long currentUserId = jwtUtil.extractUserId(token);
            System.out.println("Extracted userId from token: " + currentUserId); // Log userId từ token

            // Kiểm tra xem người dùng có quyền chỉnh sửa hồ sơ của mình không
            if (!userId.equals(currentUserId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Trả về 403 Forbidden nếu không phải chính mình
            }

            // Gọi service để cập nhật hồ sơ
            System.out.println("Calling userProfileService.updateProfile...");
            UserProfile updatedUserProfile = userProfileService.updateProfile(userId, updatedProfile);

            // Kiểm tra lại sau khi cập nhật
            System.out.println("Updated user profile: " + updatedUserProfile);
            return new ResponseEntity<>(updatedUserProfile, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Error in updateUserProfile: " + e.getMessage()); // Log lỗi để dễ dàng xác định
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Trả về 404 Not Found nếu không tìm thấy user
        }
    }

    // Lấy tất cả hồ sơ user (dành cho admin)
    @GetMapping("/admin/all")
    public ResponseEntity<List<UserProfile>> getAllUserProfiles(@RequestHeader("Authorization") String token) {
        try {
            // Lấy thông tin người dùng từ JWT
            String role = jwtUtil.extractRole(token);

            // Chỉ cho phép admin lấy danh sách
            if (!role.equals("ROLE_ADMIN")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Trả về 403 Forbidden nếu không phải admin
            }

            List<UserProfile> userProfiles = userProfileService.getAllProfiles();
            return new ResponseEntity<>(userProfiles, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Trả về 500 nếu có lỗi xảy ra
        }
    }
}
