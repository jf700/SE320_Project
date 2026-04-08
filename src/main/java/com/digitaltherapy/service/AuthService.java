package com.digitaltherapy.service;

import com.digitaltherapy.dto.AuthResponse;
import com.digitaltherapy.dto.LoginRequest;
import com.digitaltherapy.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String accessToken);
}