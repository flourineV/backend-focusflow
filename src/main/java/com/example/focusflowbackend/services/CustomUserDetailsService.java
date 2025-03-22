package com.example.focusflowbackend.services;

import com.example.focusflowbackend.models.User;
import com.example.focusflowbackend.repository.UserAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Đánh dấu đây là một Bean
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserAccountRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Loại bỏ tiền tố "ROLE_" nếu có
        String role = user.getRole().name().startsWith("ROLE_")
                ? user.getRole().name().substring(5) : user.getRole().name();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(role) // Sử dụng role không có "ROLE_" tiền tố
                .build();
    }

}
