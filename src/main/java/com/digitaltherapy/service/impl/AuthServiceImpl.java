package com.digitaltherapy.service.impl;

import com.digitaltherapy.dto.request.AuthRequests.*;
import com.digitaltherapy.dto.response.AuthResponses.*;
import com.digitaltherapy.entity.User;
import com.digitaltherapy.exception.BadRequestException;
import com.digitaltherapy.exception.ConflictException;
import com.digitaltherapy.repository.UserRepository;
import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.security.TokenBlacklist;
import com.digitaltherapy.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider, TokenBlacklist tokenBlacklist) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider; this.tokenBlacklist = tokenBlacklist;
    }

    @Override @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email()))
            throw new ConflictException("Email already registered: " + request.email());
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        log.info("Registered: {}", user.getEmail());
        return buildResponse(user);
    }

    @Override @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new BadRequestException("Invalid email or password");
        log.info("Login: {}", user.getEmail());
        return buildResponse(user);
    }

    @Override @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken))
            throw new BadRequestException("Invalid or expired refresh token");
        User user = userRepository.findById(tokenProvider.getUserIdFromToken(refreshToken))
                .orElseThrow(() -> new BadRequestException("User not found"));
        return buildResponse(user);
    }

    @Override
    public void logout(String accessToken) { tokenBlacklist.add(accessToken); }

    private AuthResponse buildResponse(User user) {
        String access = tokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refresh = tokenProvider.generateRefreshToken(user.getId());
        return AuthResponse.of(access, refresh, 3600L,
                new UserInfo(user.getId(), user.getName(), user.getEmail(),
                        Boolean.TRUE.equals(user.getOnboardingComplete())));
    }
}
