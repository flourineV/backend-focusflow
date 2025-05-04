package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.security.JwtUtil;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.dto.auth.AuthenticationRequest;
import com.example.focusflowbackend.dto.auth.AuthenticationResponse;
import com.example.focusflowbackend.dto.auth.RegisterRequest;
import com.example.focusflowbackend.dto.auth.TokenRefreshRequest;
import com.example.focusflowbackend.dto.auth.TokenRefreshResponse;
import com.example.focusflowbackend.dto.response.ErrorResponse;
import com.example.focusflowbackend.dto.response.MessageResponse;
import com.example.focusflowbackend.services.AuthenticationService;
import com.example.focusflowbackend.dto.auth.ForgotPasswordRequest;
import com.example.focusflowbackend.dto.auth.ResetPasswordRequest;
import com.example.focusflowbackend.services.PasswordResetService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthorizationUtils authUtils;
    private final PasswordResetService passwordResetService;

    public AuthenticationController(AuthenticationService authenticationService, JwtUtil jwtUtil, AuthorizationUtils authUtils, PasswordResetService passwordResetService) {
        this.authenticationService = authenticationService;
        this.authUtils = authUtils;
        this.passwordResetService = passwordResetService;
    }

    //Đăng ký tài khoản
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    //Đăng nhập tài khoản
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    //Lấy RefreshToken để khôi phục AccessToken
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            TokenRefreshResponse tokenRefreshResponse = authenticationService.refreshToken(refreshToken);
            return ResponseEntity.ok(tokenRefreshResponse);
        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(new ErrorResponse(ex.getReason()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unexpected error occurred: " + ex.getMessage()));
        }
    }

    //Đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestParam Long userId) {
        try {
            authenticationService.logout(userId);
            return ResponseEntity.ok(new MessageResponse("Log out successful!"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to logout: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return authUtils.createForbiddenStringResponse();
            }

            authenticationService.deleteUser(userId);
            return ResponseEntity.ok().body("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Forgot Password
    @PostMapping("/forgotpassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            PasswordResetService.PasswordResetTokenResult result
                    = passwordResetService.createPasswordResetTokenForUser(request.getEmail());

            if (result.isEmailSent()) {
                return ResponseEntity.ok(new MessageResponse("Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn."));
            } else {
                // Báo lỗi nếu không gửi được email
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể gửi email. Vui lòng thử lại sau.");
            }
        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(new ErrorResponse(ex.getReason()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi không xác định: " + ex.getMessage()));
        }
    }

    // Reset Password
    @PostMapping("/resetpassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new MessageResponse("Mật khẩu đã được đặt lại thành công"));
        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(new ErrorResponse(ex.getReason()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi không xác định: " + ex.getMessage()));
        }
    }
}
