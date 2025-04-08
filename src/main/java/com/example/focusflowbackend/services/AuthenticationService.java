package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.auth.AuthenticationRequest;
import com.example.focusflowbackend.dto.auth.AuthenticationResponse;
import com.example.focusflowbackend.dto.auth.RegisterRequest;
import com.example.focusflowbackend.dto.auth.TokenRefreshResponse;
import com.example.focusflowbackend.models.Role;
import com.example.focusflowbackend.models.Setting;
import com.example.focusflowbackend.models.Status;
import com.example.focusflowbackend.models.User;
import com.example.focusflowbackend.models.UserProfile;
import com.example.focusflowbackend.models.Gender;
import com.example.focusflowbackend.models.Notification;
import com.example.focusflowbackend.models.RefreshToken;
import com.example.focusflowbackend.repository.UserAccountRepo;
import com.example.focusflowbackend.repository.UserProfileRepo;
import com.example.focusflowbackend.repository.SettingRepo;
import com.example.focusflowbackend.repository.NotificationRepo;
import com.example.focusflowbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthenticationService {

    @Autowired
    private UserAccountRepo userRepository;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private SettingRepo settingRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // Kiểm tra nếu email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Tạo người dùng mới
        Role role = Role.ROLE_USER; // Mặc định role
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(Status.active)
                .build();

        // Lưu người dùng vào bảng User_Account
        userRepository.save(user);

        // Tạo User_Profile cho người dùng vừa đăng ký
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);  // Liên kết user
        userProfile.setUsername(request.getEmail().split("@")[0]);  // Tạo username từ email (hoặc có thể tùy chỉnh)
        userProfile.setBio("This is " + user.getEmail() + "'s bio.");
        userProfile.setPhone("Not provided");
        userProfile.setBirthdate(null);  // Có thể thay đổi nếu có giá trị
        userProfile.setGender(Gender.OTHER);  // Mặc định là OTHER, có thể thay đổi nếu có giá trị
        userProfile.setCountry("Not provided");

        // Tạo Setting mặc định cho user
        Setting setting = Setting.builder()
                .user(user) // Liên kết với user mới tạo
                .language("vi")
                .theme("light")
                .taskReminder(true)
                .notificationEnabled(true)
                .pomodoroDuration(25)
                .shortBreak(5)
                .longBreak(15)
                .pomodoroRounds(4)
                .timezone("UTC+7")
                .build();

        Notification welcome = Notification.builder()
                .user(user)
                .type("WELCOME")
                .message("Chào mừng bạn đến với FocusFlow! 🎉")
                .isRead(false)
                .build();

        //Lưu database
        userProfileRepo.save(userProfile);
        settingRepo.save(setting);
        notificationRepo.save(welcome);

        // Tạo JWT token cho người dùng mới
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        // Tạo refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // Trả về token trong AuthenticationResponse
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Tạo token với role dưới dạng String
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        // Tạo refresh token mới
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public TokenRefreshResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
                    return new TokenRefreshResponse(token, refreshToken);
                })
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Refresh token not found in database!"));
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteByUserId(userId);
    }

    @Transactional
    public void deleteUser(Long userId) {
        // Kiểm tra xem người dùng có tồn tại không
        if (userRepository.existsById(userId)) {
            // Xóa refresh token trước
            refreshTokenService.deleteByUserId(userId);
            // Xóa người dùng
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
