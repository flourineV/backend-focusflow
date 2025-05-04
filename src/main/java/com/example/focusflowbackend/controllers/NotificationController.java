package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.models.Notification;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthorizationUtils authUtils;

    // GET all notifications for user
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    // GET unread notifications for user
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    // PUT mark as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long notificationId) {
        // Lấy userId từ token
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Lấy thông tin của notification để kiểm tra quyền
        Notification notification = notificationService.getNotificationById(notificationId);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        // Kiểm tra xem notification có thuộc về user không
        if (!notification.getUser().getId().equals(userId) && !authUtils.isAdmin(token)) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(notificationService.markAsRead(notificationId));
    }

    // DELETE notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @RequestHeader("Authorization") String token,
            @PathVariable Long notificationId) {
        // Lấy userId từ token
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Lấy thông tin của notification để kiểm tra quyền
        Notification notification = notificationService.getNotificationById(notificationId);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        // Kiểm tra xem notification có thuộc về user không
        if (!notification.getUser().getId().equals(userId) && !authUtils.isAdmin(token)) {
            return authUtils.createForbiddenResponse();
        }

        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
