package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.AuthenticationRequest;
import com.example.focusflowbackend.dto.AuthenticationResponse;
import com.example.focusflowbackend.dto.RegisterRequest;
import com.example.focusflowbackend.models.Role;
import com.example.focusflowbackend.models.Status;
import com.example.focusflowbackend.models.User;
import com.example.focusflowbackend.models.UserProfile;
import com.example.focusflowbackend.models.Gender;
import com.example.focusflowbackend.repository.UserAccountRepo;
import com.example.focusflowbackend.repository.UserProfileRepo;
import com.example.focusflowbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthenticationService {

    @Autowired
    private UserAccountRepo userRepository;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationResponse register(RegisterRequest request) {
        // Kiểm tra nếu email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Tạo người dùng mới
        Role role = Role.valueOf("ROLE_" + request.getRole().toUpperCase());
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

        // Lưu thông tin User_Profile
        userProfileRepo.save(userProfile);

        // Tạo JWT token cho người dùng mới
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name()); // Chuyển đổi Role thành String

        // Trả về token trong AuthenticationResponse
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Tạo token với role dưới dạng String
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        return new AuthenticationResponse(token);
    }

    public void deleteUser(Long userId) {
        // Kiểm tra xem người dùng có tồn tại không
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId); // Xóa người dùng
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
