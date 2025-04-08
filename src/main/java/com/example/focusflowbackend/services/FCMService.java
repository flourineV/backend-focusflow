package com.example.focusflowbackend.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    public void sendAppStatusNotification(String fcmToken, String userId, boolean isOnline) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("App Status")
                        .setBody(isOnline ? "App is online" : "App is offline")
                        .build())
                .putData("userId", userId)
                .putData("status", isOnline ? "online" : "offline")
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            System.out.println("Error sending FCM message: " + e.getMessage());
        }
    }
} 