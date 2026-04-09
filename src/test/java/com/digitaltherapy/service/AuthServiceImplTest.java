package com.digitaltherapy.service;

import com.digitaltherapy.dto.request.AuthRequests.*;
import com.digitaltherapy.dto.response.AuthResponses.*;
import com.digitaltherapy.entity.User;
import com.digitaltherapy.exception.BadRequestException;
import com.digitaltherapy.exception.ConflictException;
import com.digitaltherapy.repository.UserRepository;
import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.security.TokenBlacklist;
import com.digitaltherapy.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider tokenProvider;
    @Mock TokenBlacklist tokenBlacklist;

    @InjectMocks AuthServiceImpl authService;

    private User sampleUser;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(userId);
        sampleUser.setName("Alice");
        sampleUser.setEmail("alice@example.com");
        sampleUser.setPasswordHash("$2a$hashed");
        sampleUser.setOnboardingComplete(false);
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_success_returnsAuthResponse() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateAccessToken(userId, "alice@example.com")).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(userId)).thenReturn("refresh-token");

        AuthResponse result = authService.register(
                new RegisterRequest("Alice", "alice@example.com", "password123"));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.user().name()).isEqualTo("Alice");
        assertThat(result.user().email()).isEqualTo("alice@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsConflict_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("Alice", "alice@example.com", "password123")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already registered");
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_success_returnsAuthResponse() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("password123", "$2a$hashed")).thenReturn(true);
        when(tokenProvider.generateAccessToken(userId, "alice@example.com")).thenReturn("access");
        when(tokenProvider.generateRefreshToken(userId)).thenReturn("refresh");

        AuthResponse result = authService.login(new LoginRequest("alice@example.com", "password123"));

        assertThat(result.accessToken()).isEqualTo("access");
        assertThat(result.user().id()).isEqualTo(userId);
    }

    @Test
    void login_throwsBadRequest_whenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("wrong@example.com", "pass")))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_throwsBadRequest_whenPasswordWrong() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongpass", "$2a$hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("alice@example.com", "wrongpass")))
                .isInstanceOf(BadRequestException.class);
    }

    // ── refreshToken ──────────────────────────────────────────────────────────

    @Test
    void refreshToken_success() {
        when(tokenProvider.validateToken("refresh-tok")).thenReturn(true);
        when(tokenProvider.isRefreshToken("refresh-tok")).thenReturn(true);
        when(tokenProvider.getUserIdFromToken("refresh-tok")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(tokenProvider.generateAccessToken(userId, "alice@example.com")).thenReturn("new-access");
        when(tokenProvider.generateRefreshToken(userId)).thenReturn("new-refresh");

        AuthResponse result = authService.refreshToken("refresh-tok");
        assertThat(result.accessToken()).isEqualTo("new-access");
    }

    @Test
    void refreshToken_throwsBadRequest_whenTokenInvalid() {
        when(tokenProvider.validateToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken("bad-token"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid or expired");
    }

    // ── logout ────────────────────────────────────────────────────────────────

    @Test
    void logout_addsTokenToBlacklist() {
        authService.logout("some-token");
        verify(tokenBlacklist).add("some-token");
    }
}
