package com.digitaltherapy.controller;

import com.digitaltherapy.dto.response.ProgressResponses.*;
import com.digitaltherapy.exception.GlobalExceptionHandler;
import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.security.TokenBlacklist;
import com.digitaltherapy.security.UserDetailsServiceImpl;
import com.digitaltherapy.service.ProgressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ProgressController.class, GlobalExceptionHandler.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProgressControllerTest {

    @Autowired MockMvc mvc;

    @MockBean ProgressService progressService;
    @MockBean JwtTokenProvider tokenProvider;
    @MockBean TokenBlacklist tokenBlacklist;
    @MockBean UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void weekly_returns200() throws Exception {
        when(progressService.getWeeklyProgress(any()))
                .thenReturn(new WeeklyProgress(LocalDate.now(), LocalDate.now().plusDays(6), 2, 3, 1.5, 0, List.of()));
        mvc.perform(get("/progress/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionsCompleted").value(2));
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void monthly_returns200() throws Exception {
        when(progressService.getMonthlyTrends(any()))
                .thenReturn(new MonthlyTrends(4, 2026, 5, 10, 2.0, List.of(), List.of()));
        mvc.perform(get("/progress/monthly")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void burnout_returns200() throws Exception {
        when(progressService.getBurnoutRecovery(any()))
                .thenReturn(new BurnoutRecovery("Action", 60, List.of(), List.of(), "Keep going!"));
        mvc.perform(get("/progress/burnout")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void achievements_returns200() throws Exception {
        when(progressService.getAchievements(any()))
                .thenReturn(new Achievements(List.of(), List.of()));
        mvc.perform(get("/progress/achievements")).andExpect(status().isOk());
    }
}
