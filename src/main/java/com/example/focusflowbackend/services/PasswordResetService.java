package com.example.focusflowbackend.services;

import com.example.focusflowbackend.models.PasswordResetToken;
import com.example.focusflowbackend.models.User;
import com.example.focusflowbackend.repository.PasswordResetTokenRepo;
import com.example.focusflowbackend.repository.UserAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserAccountRepo userRepository;

    @Autowired
    private PasswordResetTokenRepo tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    /**
     * Tạo và gửi token đặt lại mật khẩu
     * @return PasswordResetTokenResult chứa token và thông tin gửi email
     */
    @Transactional
    public PasswordResetTokenResult createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email không tồn tại"));

        // Xóa token cũ nếu có
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plus(30, ChronoUnit.MINUTES))
                .build();

        tokenRepository.save(passwordResetToken);

        // Gửi email
        boolean emailSent = emailService.sendPasswordResetEmail(user.getEmail(), token);
        
        // In token ra console để dễ dàng kiểm tra
        System.out.println("===== RESET PASSWORD TOKEN =====");
        System.out.println("Email: " + email);
        System.out.println("Token: " + token);
        System.out.println("================================");
        
        return new PasswordResetTokenResult(token, emailSent);
    }

    /**
     * Xác nhận token và đặt lại mật khẩu
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token không hợp lệ"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token đã hết hạn");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa token sau khi sử dụng
        tokenRepository.delete(resetToken);
    }
    
    /**
     * Scheduled task chạy mỗi giờ để xóa tất cả token đã hết hạn
     */
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ (3,600,000 ms)
    @Transactional
    public void purgeExpiredTokens() {
        int deletedCount = tokenRepository.deleteAllExpiredTokensBefore(Instant.now());
        if (deletedCount > 0) {
            System.out.println("Đã xóa " + deletedCount + " token đặt lại mật khẩu đã hết hạn");
        }
    }
    
    /**
     * Class kết quả chứa token và trạng thái gửi email
     */
    public static class PasswordResetTokenResult {
        private final String token;
        private final boolean emailSent;
        
        public PasswordResetTokenResult(String token, boolean emailSent) {
            this.token = token;
            this.emailSent = emailSent;
        }
        
        public String getToken() {
            return token;
        }
        
        public boolean isEmailSent() {
            return emailSent;
        }
    }
} 