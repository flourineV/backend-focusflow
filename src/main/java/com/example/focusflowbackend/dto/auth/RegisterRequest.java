package com.example.focusflowbackend.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RegisterRequest {

    private String email;
    private String password;
}
