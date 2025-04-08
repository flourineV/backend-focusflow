package com.example.focusflowbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    
    public AuthenticationResponse(String token) {
        this.token = token;
    }
}
