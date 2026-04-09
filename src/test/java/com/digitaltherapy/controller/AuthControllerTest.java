package com.digitaltherapy.controller;

import com.digitaltherapy.dto.request.AuthRequests.*;
import com.digitaltherapy.dto.response.AuthResponses.*;
import com.digitaltherapy.exception.GlobalExceptionHandler;
import com.digitaltherapy.security.JwtAuthenticationFilter;
import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.security.TokenBlacklist;
import com.digitaltherapy.security.UserDetailsServiceImpl;
import com.digitaltherapy.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class, GlobalExceptionHandler.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @MockBean AuthService authService;
    @MockBean JwtTokenProvider tokenProvider;
    @MockBean TokenBlacklist tokenBlacklist;
    @MockBean UserDetailsServiceImpl userDetailsService;

    private static final UUID USER_ID = UUID.randomUUID();

    private AuthResponse sampleAuthResponse() {
        return AuthResponse.of(
                "access-token", "refresh-token", 3600L,
                new UserInfo(USER_ID, "Alice", "alice@example.com", false));
    }

    @Test
    void register_returns201_whenValid() throws Exception {
        when(authService.register(any())).thenReturn(sampleAuthResponse());

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new RegisterRequest("Alice", "alice@example.com", "password123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.user.name").value("Alice"));
    }

    @Test
    void register_returns422_whenEmailInvalid() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new RegisterRequest("Alice", "not-an-email", "password123"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void register_returns422_whenPasswordTooShort() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new RegisterRequest("Alice", "alice@example.com", "short"))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void login_returns200_whenValid() throws Exception {
        when(authService.login(any())).thenReturn(sampleAuthResponse());

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new LoginRequest("alice@example.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void logout_returns204_whenValid() throws Exception {
        doNothing().when(authService).logout("access-token");

        mvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new LogoutRequest("access-token"))))
                .andExpect(status().isNoContent());
    }
}
