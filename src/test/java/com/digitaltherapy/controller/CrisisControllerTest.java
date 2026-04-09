package com.digitaltherapy.controller;

import com.digitaltherapy.dto.response.CrisisResponses.*;
import com.digitaltherapy.exception.GlobalExceptionHandler;
import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.security.TokenBlacklist;
import com.digitaltherapy.security.UserDetailsServiceImpl;
import com.digitaltherapy.service.CrisisService;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CrisisController.class, GlobalExceptionHandler.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class CrisisControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @MockBean CrisisService crisisService;
    @MockBean JwtTokenProvider tokenProvider;
    @MockBean TokenBlacklist tokenBlacklist;
    @MockBean UserDetailsServiceImpl userDetailsService;

    @Test
    void hub_returns200() throws Exception {
        when(crisisService.getCrisisHub())
                .thenReturn(new CrisisHub("Help is available", List.of(), List.of(), "988"));
        mvc.perform(get("/crisis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotlineNumber").value("988"));
    }

    @Test
    void copingStrategies_returns200() throws Exception {
        when(crisisService.getCopingStrategies()).thenReturn(List.of());
        mvc.perform(get("/crisis/coping-strategies")).andExpect(status().isOk());
    }

    @Test
    void detect_returns200_safe() throws Exception {
        when(crisisService.detectCrisis(any()))
                .thenReturn(new CrisisDetectionResult(RiskLevel.none, List.of(), RecommendedAction.none, "No indicators."));
        mvc.perform(post("/crisis/detect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"I had a great day\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("none"));
    }

    @Test
    void detect_returns200_critical() throws Exception {
        when(crisisService.detectCrisis(any()))
                .thenReturn(new CrisisDetectionResult(RiskLevel.critical, List.of("suicide"), RecommendedAction.immediate_intervention, "Crisis detected."));
        mvc.perform(post("/crisis/detect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"test crisis text\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("critical"));
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void getSafetyPlan_returns200() throws Exception {
        when(crisisService.getSafetyPlan(any()))
                .thenReturn(new SafetyPlan(List.of(), List.of(), List.of(), List.of(), List.of(), "My reasons"));
        mvc.perform(get("/crisis/safety-plan")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void updateSafetyPlan_returns200() throws Exception {
        when(crisisService.updateSafetyPlan(any(), any()))
                .thenReturn(new SafetyPlan(List.of("signal"), List.of(), List.of(), List.of(), List.of(), "reasons"));
        mvc.perform(put("/crisis/safety-plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"warningSignals\":[\"signal\"],\"reasonsForLiving\":\"reasons\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warningSignals[0]").value("signal"));
    }
}
