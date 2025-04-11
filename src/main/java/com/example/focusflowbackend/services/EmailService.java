package com.example.focusflowbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendPasswordResetEmail(String to, String token) {
        try {
            // Thử gửi bằng MimeMessage (hỗ trợ HTML và đính kèm)
            return sendMimeMessage(to, token);
        } catch (Exception e) {
            System.err.println("Lỗi gửi MimeMessage đến " + to + ": " + e.getMessage());
            
            try {
                // Thử lại với SimpleMailMessage
                return sendSimpleMessage(to, token);
            } catch (Exception e2) {
                System.err.println("Lỗi gửi SimpleMailMessage đến " + to + ": " + e2.getMessage());
                e2.printStackTrace();
                return false;
            }
        }
    }
    
    private boolean sendMimeMessage(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("FocusFlow - Mã đặt lại mật khẩu của bạn");
        
        // URL cho ứng dụng Android - dùng deep link
        String resetUrl = "focusflow://reset-password?token=" + token;
        
        String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2 style='color: #4a86e8;'>Đặt lại mật khẩu FocusFlow</h2>"
                + "<p>Xin chào,</p>"
                + "<p>Bạn vừa yêu cầu đặt lại mật khẩu cho tài khoản FocusFlow.</p>"
                + "<p>Đây là mã đặt lại mật khẩu của bạn:</p>"
                + "<div style='background-color: #f2f2f2; padding: 10px; border-radius: 5px; margin: 15px 0;'>"
                + "<code style='font-size: 16px;'>" + token + "</code>"
                + "</div>"
                + "<p>Mã này có hiệu lực trong 30 phút.</p>"
                + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
                + "<p>Trân trọng,<br>Đội ngũ FocusFlow</p>"
                + "<p style='color: #666; font-size: 12px; margin-top: 20px;'>Email này được gửi tự động, vui lòng không trả lời.</p>"
                + "</div>";
        
        helper.setText(htmlContent, true);
        
        try {
            mailSender.send(message);
            System.out.println("HTML Email gửi thành công đến: " + to);
            return true;
        } catch (MailException e) {
            System.err.println("Lỗi gửi HTML email: " + e.getMessage());
            throw e;
        }
    }
    
    private boolean sendSimpleMessage(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("FocusFlow - Mã đặt lại mật khẩu của bạn");
        
        message.setText("Xin chào,\n\n" +
                "Bạn vừa yêu cầu đặt lại mật khẩu cho tài khoản FocusFlow. " +
                "Đây là mã đặt lại mật khẩu của bạn:\n\n" +
                token + "\n\n" +
                "Mã này có hiệu lực trong 30 phút.\n\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ FocusFlow");
        
        try {
            mailSender.send(message);
            System.out.println("Text Email gửi thành công đến: " + to);
            return true;
        } catch (MailException e) {
            System.err.println("Lỗi gửi text email: " + e.getMessage());
            throw e;
        }
    }
} 