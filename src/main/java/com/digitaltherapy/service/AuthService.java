package com.digitaltherapy.service;

import com.digitaltherapy.dto.request.AuthRequests.*;
import com.digitaltherapy.dto.response.AuthResponses.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String accessToken);
}
