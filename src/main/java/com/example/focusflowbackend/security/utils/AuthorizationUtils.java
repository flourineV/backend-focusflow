package com.example.focusflowbackend.security.utils;

import com.example.focusflowbackend.dto.pomodoro.pomodoroDTO;
import com.example.focusflowbackend.dto.response.ErrorResponse;
import com.example.focusflowbackend.dto.response.MessageResponse;
import com.example.focusflowbackend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Lớp tiện ích cung cấp các phương thức kiểm tra quyền truy cập
 * cho tất cả các controller trong ứng dụng.
 */
@Component
@RequiredArgsConstructor
public class AuthorizationUtils {

    private final JwtUtil jwtUtil;

    /**
     * Kiểm tra xem người dùng hiện tại có phải là admin hoặc chính người dùng được truy cập
     * 
     * @param token Token JWT từ header Authorization
     * @param userId ID của người dùng cần truy cập
     * @return true nếu người dùng hiện tại là admin hoặc chính người dùng được truy cập
     */
    public boolean isAdminOrSameUser(String token, Long userId) {
        try {
            Long tokenUserId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);
            
            return role.equals("ROLE_ADMIN") || tokenUserId.equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là admin không
     * 
     * @param token Token JWT từ header Authorization
     * @return true nếu người dùng hiện tại là admin
     */
    public boolean isAdmin(String token) {
        try {
            String role = jwtUtil.extractRole(token);
            return role.equals("ROLE_ADMIN");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Trích xuất ID của người dùng từ token
     * 
     * @param token Token JWT từ header Authorization
     * @return ID của người dùng hoặc null nếu không thể trích xuất
     */
    public Long getCurrentUserId(String token) {
        try {
            return jwtUtil.extractUserId(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Tạo ResponseEntity với lỗi 403 Forbidden cho các endpoint trả về đối tượng đơn
     * 
     * @param <T> Kiểu dữ liệu trả về
     * @return ResponseEntity với lỗi 403 Forbidden
     */
    public <T> ResponseEntity<T> createForbiddenResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Tạo ResponseEntity với lỗi 403 Forbidden và ErrorResponse
     * 
     * @return ResponseEntity với lỗi 403 Forbidden và ErrorResponse
     */
    public ResponseEntity<ErrorResponse> createForbiddenErrorResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Access denied: You do not have permission to access this resource"));
    }

    /**
     * Tạo ResponseEntity với lỗi 403 Forbidden và thông báo lỗi tùy chỉnh
     * 
     * @param message Thông báo lỗi
     * @return ResponseEntity với lỗi 403 Forbidden và ErrorResponse
     */
    public ResponseEntity<ErrorResponse> createForbiddenErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(message));
    }

    /**
     * Tạo ResponseEntity với lỗi 403 Forbidden và MessageResponse
     * 
     * @return ResponseEntity với lỗi 403 Forbidden và MessageResponse
     */
    public ResponseEntity<MessageResponse> createForbiddenMessageResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageResponse("Access denied: You do not have permission to access this resource"));
    }

    /**
     * Tạo ResponseEntity với lỗi 403 Forbidden và thông báo lỗi String
     * 
     * @return ResponseEntity với lỗi 403 Forbidden và String
     */
    public ResponseEntity<String> createForbiddenStringResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied: You do not have permission to access this resource");
    }

    /**
     * Tạo ResponseEntity với lỗi 403 Forbidden và pomodoroDTO.Response
     * Sử dụng cho PomodoroController
     * 
     * @return ResponseEntity với lỗi 403 Forbidden và pomodoroDTO.Response
     */
    public ResponseEntity<pomodoroDTO.Response> createForbiddenPomodoroResponse() {
        pomodoroDTO.Response errorResponse = pomodoroDTO.Response.builder()
                .id(-1L)
                .note("Access denied: Invalid user")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
} 