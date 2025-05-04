package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.models.UserProfile;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.services.UserProfileService;
import com.example.focusflowbackend.services.AuthenticationService;
import com.example.focusflowbackend.services.UserActivityService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserProfileService userProfileService;
    private final AuthorizationUtils authUtils;
    private final AuthenticationService authenticationService;

    @Autowired
    private UserActivityService activityService;

    // Lấy DAU theo ngày cụ thể
    @GetMapping("/dau")
    public ResponseEntity<?> getDAU(@RequestParam String date) {
        try {
            LocalDate specificDate = LocalDate.parse(date);  // chuyển đổi chuỗi ngày sang LocalDate
            long dau = activityService.getDAU(specificDate);
            return ResponseEntity.ok("DAU ngày " + date + ": " + dau);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày không hợp lệ");
        }
    }

    // Lấy MAU theo tháng cụ thể
    @GetMapping("/mau")
    public ResponseEntity<?> getMAU(@RequestParam String month) {
        try {
            YearMonth specificMonth = YearMonth.parse(month);  // chuyển đổi chuỗi tháng sang YearMonth
            long mau = activityService.getMAU(specificMonth);
            return ResponseEntity.ok("MAU tháng " + month + ": " + mau);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tháng không hợp lệ");
        }
    }

    // Lấy YAU theo năm cụ thể
    @GetMapping("/yau")
    public ResponseEntity<?> getYAU(@RequestParam String year) {
        try {
            Year specificYear = Year.parse(year);  // chuyển đổi chuỗi năm sang Year
            long yau = activityService.getYAU(specificYear);
            return ResponseEntity.ok("YAU năm " + year + ": " + yau);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Năm không hợp lệ");
        }
    }

    // Lấy user profile theo ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserProfileById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ Admin mới có quyền truy cập API này");
        }

        try {
            UserProfile userProfile = userProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user với ID: " + userId);
        }
    }

    // Xóa user
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ Admin mới có quyền truy cập API này");
        }

        try {
            authenticationService.deleteUser(userId);
            return ResponseEntity.ok("Đã xóa user thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa user: " + e.getMessage());
        }
    }

    // Cập nhật user profile
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestBody UserProfile updatedProfile) {
        if (!authUtils.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ Admin mới có quyền truy cập API này");
        }

        try {
            UserProfile userProfile = userProfileService.updateProfile(userId, updatedProfile);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật user: " + e.getMessage());
        }
    }
}
