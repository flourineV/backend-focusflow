package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.security.JwtUtil;
import com.example.focusflowbackend.dto.auth.AuthenticationRequest;
import com.example.focusflowbackend.dto.auth.AuthenticationResponse;
import com.example.focusflowbackend.dto.auth.RegisterRequest;
import com.example.focusflowbackend.services.AuthenticationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    public AuthenticationController(AuthenticationService authenticationService, JwtUtil jwtUtil) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            // Đăng ký người dùng và nhận token
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.ok(response);  // Trả về token trong response
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new AuthenticationResponse("Error: " + e.getMessage()));  // Trả về lỗi nếu có
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody AuthenticationRequest request) {
        try {
            // Lấy token sau khi người dùng đăng nhập thành công
            AuthenticationResponse response = authenticationService.authenticate(request); // Trả về đối tượng AuthenticationResponse

            // Trả về token dưới dạng ResponseEntity
            return ResponseEntity.ok(response); // Đảm bảo trả về AuthenticationResponse, không phải chỉ token
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new AuthenticationResponse("Error: " + e.getMessage())); // Trả về lỗi nếu có
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        try {
            // Loại bỏ "Bearer " từ token
            token = token.replace("Bearer ", "").trim();

            // Trích xuất thông tin từ token
            Long currentUserId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            // Kiểm tra quyền truy cập:
            // - Chỉ admin hoặc chính người dùng đó mới được xóa
            if (role.equals("ROLE_ADMIN") || currentUserId.equals(userId)) {
                authenticationService.deleteUser(userId);
                return ResponseEntity.ok().body("User deleted successfully");
            } else {
                return ResponseEntity.status(403).body("Forbidden: You do not have permission to delete this user");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}
