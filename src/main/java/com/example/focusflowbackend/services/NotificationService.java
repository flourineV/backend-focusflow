package com.example.focusflowbackend.services;

import com.example.focusflowbackend.models.Notification;
import com.example.focusflowbackend.repository.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    // Lấy tất cả thông báo của user
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepo.findByUserId(userId);
    }

    // Lấy thông báo chưa đọc
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepo.findByUserIdAndIsReadFalse(userId);
    }

    // Đánh dấu là đã đọc
    public Notification markAsRead(Long notificationId) {
        Notification noti = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        noti.setIsRead(true);
        return notificationRepo.save(noti);
    }

    // Xóa thông báo
    public void deleteNotification(Long notificationId) {
        if (!notificationRepo.existsById(notificationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        notificationRepo.deleteById(notificationId);
    }
}
