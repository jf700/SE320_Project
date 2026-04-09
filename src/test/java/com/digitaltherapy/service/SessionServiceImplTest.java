package com.digitaltherapy.service;

import com.digitaltherapy.dto.request.SessionRequests.*;
import com.digitaltherapy.dto.response.SessionResponses.*;
import com.digitaltherapy.entity.*;
import com.digitaltherapy.exception.BadRequestException;
import com.digitaltherapy.exception.ResourceNotFoundException;
import com.digitaltherapy.repository.*;
import com.digitaltherapy.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock CbtSessionRepository cbtSessionRepository;
    @Mock SessionModuleRepository moduleRepository;
    @Mock UserSessionRepository userSessionRepository;
    @Mock UserRepository userRepository;
    @Mock ChatMessageRepository chatMessageRepository;

    @InjectMocks SessionServiceImpl sessionService;

    private User user;
    private CbtSession cbtSession;
    private UserSession userSession;
    private final UUID userId = UUID.randomUUID();
    private final UUID cbtSessionId = UUID.randomUUID();
    private final UUID userSessionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        cbtSession = new CbtSession();
        cbtSession.setId(cbtSessionId);
        cbtSession.setTitle("Thought Challenging");
        cbtSession.setDescription("Learn to challenge thoughts");
        cbtSession.setDurationMinutes(30);
        cbtSession.setObjectives(List.of("Identify distortions"));
        cbtSession.setModalities(List.of(CbtSession.Modality.TEXT));

        userSession = new UserSession();
        userSession.setId(userSessionId);
        userSession.setUser(user);
        userSession.setCbtSession(cbtSession);
        userSession.setStatus(UserSession.SessionStatus.IN_PROGRESS);
        userSession.setStartedAt(LocalDateTime.now());
        userSession.setMoodBefore(4);
    }

    @Test
    void getSessionLibrary_returnsModulesWithSessions() {
        SessionModule module = new SessionModule();
        module.setId(UUID.randomUUID());
        module.setName("Module 1");
        module.setDescription("First module");

        when(moduleRepository.findAll()).thenReturn(List.of(module));
        when(cbtSessionRepository.findByModuleIdOrderByOrderIndexAsc(module.getId()))
                .thenReturn(List.of(cbtSession));

        List<SessionModuleDto> result = sessionService.getSessionLibrary();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Module 1");
        assertThat(result.get(0).sessions()).hasSize(1);
    }

    @Test
    void getSessionDetails_returnsDetail() {
        when(cbtSessionRepository.findById(cbtSessionId)).thenReturn(Optional.of(cbtSession));

        SessionDetail detail = sessionService.getSessionDetails(cbtSessionId);

        assertThat(detail.title()).isEqualTo("Thought Challenging");
        assertThat(detail.durationMinutes()).isEqualTo(30);
        assertThat(detail.modalities()).contains("TEXT");
    }

    @Test
    void getSessionDetails_throwsNotFound() {
        when(cbtSessionRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSessionDetails(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void startSession_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cbtSessionRepository.findById(cbtSessionId)).thenReturn(Optional.of(cbtSession));
        when(userSessionRepository.findFirstByUserIdAndStatusOrderByStartedAtDesc(userId, UserSession.SessionStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(inv -> {
            UserSession us = inv.getArgument(0);
            us.setId(userSessionId);
            return us;
        });

        ActiveSession result = sessionService.startSession(userId, cbtSessionId, new StartSessionRequest(4));

        assertThat(result.cbtSessionId()).isEqualTo(cbtSessionId);
        assertThat(result.sessionTitle()).isEqualTo("Thought Challenging");
    }

    @Test
    void startSession_closesExistingInProgressSession() {
        UserSession existing = new UserSession();
        existing.setId(UUID.randomUUID());
        existing.setStatus(UserSession.SessionStatus.IN_PROGRESS);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cbtSessionRepository.findById(cbtSessionId)).thenReturn(Optional.of(cbtSession));
        when(userSessionRepository.findFirstByUserIdAndStatusOrderByStartedAtDesc(userId, UserSession.SessionStatus.IN_PROGRESS))
                .thenReturn(Optional.of(existing));
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(inv -> {
            UserSession us = inv.getArgument(0);
            if (us.getId() == null) us.setId(userSessionId);
            return us;
        });

        sessionService.startSession(userId, cbtSessionId, null);

        assertThat(existing.getStatus()).isEqualTo(UserSession.SessionStatus.EARLY_EXIT);
    }

    @Test
    void chat_success() {
        when(userSessionRepository.findById(userSessionId)).thenReturn(Optional.of(userSession));
        when(chatMessageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChatResponse result = sessionService.chat(userSessionId, userId, "I feel stressed");

        assertThat(result.message()).isNotBlank();
        assertThat(result.role()).isEqualTo("ASSISTANT");
        verify(chatMessageRepository, times(2)).save(any());
    }

    @Test
    void chat_throwsBadRequest_whenSessionNotActive() {
        userSession.setStatus(UserSession.SessionStatus.COMPLETED);
        when(userSessionRepository.findById(userSessionId)).thenReturn(Optional.of(userSession));

        assertThatThrownBy(() -> sessionService.chat(userSessionId, userId, "hello"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void chat_throwsNotFound_whenSessionMissing() {
        when(userSessionRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.chat(UUID.randomUUID(), userId, "hello"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void endSession_success() {
        when(userSessionRepository.findById(userSessionId)).thenReturn(Optional.of(userSession));
        when(userSessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SessionSummary result = sessionService.endSession(userSessionId, new EndSessionRequest("Feeling better", 7));

        assertThat(result.status()).isEqualTo(UserSession.SessionStatus.COMPLETED);
        assertThat(result.moodAfter()).isEqualTo(7);
        assertThat(result.moodImprovement()).isEqualTo(3);
    }

    @Test
    void endSession_handlesNullRequest() {
        when(userSessionRepository.findById(userSessionId)).thenReturn(Optional.of(userSession));
        when(userSessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SessionSummary result = sessionService.endSession(userSessionId, null);

        assertThat(result.status()).isEqualTo(UserSession.SessionStatus.COMPLETED);
    }

    @Test
    void getSessionHistory_returnsList() {
        when(userSessionRepository.findByUserIdOrderByStartedAtDesc(userId))
                .thenReturn(List.of(userSession));

        List<SessionHistoryEntry> history = sessionService.getSessionHistory(userId);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).sessionTitle()).isEqualTo("Thought Challenging");
    }

    @Test
    void getSessionHistory_handlesNullCbtSession() {
        UserSession orphan = new UserSession();
        orphan.setId(UUID.randomUUID());
        orphan.setStartedAt(LocalDateTime.now());
        orphan.setStatus(UserSession.SessionStatus.COMPLETED);

        when(userSessionRepository.findByUserIdOrderByStartedAtDesc(userId))
                .thenReturn(List.of(orphan));

        List<SessionHistoryEntry> history = sessionService.getSessionHistory(userId);
        assertThat(history.get(0).sessionTitle()).isEqualTo("Unknown");
    }
}
