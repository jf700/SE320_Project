package com.digitaltherapy.service.impl;

import com.digitaltherapy.dto.AuthResponse;
import com.digitaltherapy.dto.LoginRequest;
import com.digitaltherapy.dto.RegisterRequest;
import com.digitaltherapy.entity.User;
import com.digitaltherapy.exception.DuplicateResourceException;
import com.digitaltherapy.repository.UserRepository;
//import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // simple in-memory token blacklist
    private final Set<String> blacklistedTokens =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        log.info("Registering user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + request.getEmail()
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .onboardingComplete(false)
                .streakDays(0)
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getId());
        //String accessToken = "temp-access-token";
        //String refreshToken = "temp-refresh-token";

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("Logging in user: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        //String accessToken = "temp-token";
        //String refreshToken = "temp-refresh";

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        /*
        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        String email = jwtTokenProvider.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtTokenProvider.generateToken(user);

        return new AuthResponse(newAccessToken, refreshToken);
        */

        // temp
        String newAccessToken = "temp-access-token";

        return new AuthResponse(newAccessToken, refreshToken);
    }

    @Override
    public void logout(String accessToken) {
        blacklistedTokens.add(accessToken);
        log.info("Token blacklisted successfully");
    }

    // optional helper (you’ll use later in JWT filter)
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
