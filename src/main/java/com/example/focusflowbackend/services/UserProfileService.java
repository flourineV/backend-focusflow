package com.example.focusflowbackend.services;

import com.example.focusflowbackend.models.UserProfile;
import com.example.focusflowbackend.repository.UserProfileRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepo userProfileRepository;

    public UserProfileService(UserProfileRepo userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    // Lấy hồ sơ user theo user_id
    public UserProfile getProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
    }

    // Cập nhật hồ sơ user
    public UserProfile updateProfile(Long userId, UserProfile updatedProfile) {
        UserProfile existingProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        existingProfile.setUsername(updatedProfile.getUsername());
        existingProfile.setAvatar(updatedProfile.getAvatar());
        existingProfile.setBio(updatedProfile.getBio());
        existingProfile.setPhone(updatedProfile.getPhone());
        existingProfile.setBirthdate(updatedProfile.getBirthdate());
        existingProfile.setGender(updatedProfile.getGender());
        existingProfile.setCountry(updatedProfile.getCountry());

        return userProfileRepository.save(existingProfile);
    }

    // Lấy tất cả hồ sơ người dùng (dành cho admin)
    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll(); // Lấy tất cả người dùng
    }
}
