package com.digitaltherapy.controller;

import com.digitaltherapy.dto.response.SessionResponses.*;
import com.digitaltherapy.entity.UserSession;
import com.digitaltherapy.exception.GlobalExceptionHandler;
import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.security.TokenBlacklist;
import com.digitaltherapy.security.UserDetailsServiceImpl;
import com.digitaltherapy.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {SessionController.class, GlobalExceptionHandler.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @MockBean SessionService sessionService;
    @MockBean JwtTokenProvider tokenProvider;
    @MockBean TokenBlacklist tokenBlacklist;
    @MockBean UserDetailsServiceImpl userDetailsService;

    @Test
    void getLibrary_returns200() throws Exception {
        when(sessionService.getSessionLibrary()).thenReturn(List.of());
        mvc.perform(get("/sessions")).andExpect(status().isOk());
    }

    @Test
    void getDetails_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(sessionService.getSessionDetails(id))
                .thenReturn(new SessionDetail(id, "Title", "Desc", 30, List.of(), List.of()));
        mvc.perform(get("/sessions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void start_returns201() throws Exception {
        UUID sessionId = UUID.randomUUID();
        when(sessionService.startSession(any(), eq(sessionId), any()))
                .thenReturn(new ActiveSession(UUID.randomUUID(), sessionId, "Title", LocalDateTime.now(), "IN_PROGRESS"));
        mvc.perform(post("/sessions/" + sessionId + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"moodBefore\":4}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void history_returns200() throws Exception {
        when(sessionService.getSessionHistory(any())).thenReturn(List.of());
        mvc.perform(get("/sessions/history")).andExpect(status().isOk());
    }
}
