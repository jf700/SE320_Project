package com.digitaltherapy.controller;

import com.digitaltherapy.dto.response.DiaryResponses.*;
import com.digitaltherapy.exception.GlobalExceptionHandler;
import com.digitaltherapy.security.JwtTokenProvider;
import com.digitaltherapy.security.TokenBlacklist;
import com.digitaltherapy.security.UserDetailsServiceImpl;
import com.digitaltherapy.service.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {DiaryController.class, GlobalExceptionHandler.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class DiaryControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @MockBean DiaryService diaryService;
    @MockBean JwtTokenProvider tokenProvider;
    @MockBean TokenBlacklist tokenBlacklist;
    @MockBean UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void listEntries_returns200() throws Exception {
        when(diaryService.getEntries(any(), any())).thenReturn(Page.empty());
        mvc.perform(get("/diary/entries")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void createEntry_returns201() throws Exception {
        UUID entryId = UUID.randomUUID();
        when(diaryService.createEntry(any(), any()))
                .thenReturn(new DiaryEntryResponse(entryId, "Situation", "Thought", List.of(), List.of(), null, 3, 5, null, null, LocalDateTime.now()));
        mvc.perform(post("/diary/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"situation\":\"Test\",\"automaticThought\":\"Thought\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getEntry_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(diaryService.getEntryDetail(id))
                .thenReturn(new DiaryEntryResponse(id, "Sit", "Thought", List.of(), List.of(), null, 3, 5, null, null, LocalDateTime.now()));
        mvc.perform(get("/diary/entries/" + id)).andExpect(status().isOk());
    }

    @Test
    void deleteEntry_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(diaryService).deleteEntry(id);
        mvc.perform(delete("/diary/entries/" + id)).andExpect(status().isNoContent());
    }

    @Test
    void suggestDistortions_returns200() throws Exception {
        when(diaryService.suggestDistortions(any())).thenReturn(List.of());
        mvc.perform(post("/diary/distortions/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"thought\":\"I always fail\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void insights_returns200() throws Exception {
        when(diaryService.getInsights(any()))
                .thenReturn(new DiaryInsights(5, 1.5, List.of(), List.of(), "Keep going!"));
        mvc.perform(get("/diary/insights")).andExpect(status().isOk());
    }
}
