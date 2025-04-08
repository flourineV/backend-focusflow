package com.example.focusflowbackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FCMConfig {

    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("focusflow_backend\\src\\main\\resources\\FCM\\focusflow-e227d-firebase-adminsdk-fbsvc-7e804e6e3e.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            System.out.println("Error initializing Firebase: " + e.getMessage());
        }
    }
}
