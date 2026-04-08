package com.digitaltherapy.service.impl;

import com.digitaltherapy.dto.request.SessionRequests.*;
import com.digitaltherapy.dto.response.SessionResponses.*;
import com.digitaltherapy.entity.*;
import com.digitaltherapy.exception.BadRequestException;
import com.digitaltherapy.exception.ResourceNotFoundException;
import com.digitaltherapy.repository.*;
import com.digitaltherapy.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {
    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);
    private final CbtSessionRepository cbtSessionRepository;
    private final SessionModuleRepository moduleRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public SessionServiceImpl(CbtSessionRepository cbtSessionRepository, SessionModuleRepository moduleRepository,
                              UserSessionRepository userSessionRepository, UserRepository userRepository,
                              ChatMessageRepository chatMessageRepository) {
        this.cbtSessionRepository = cbtSessionRepository; this.moduleRepository = moduleRepository;
        this.userSessionRepository = userSessionRepository; this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override @Transactional(readOnly = true)
    public List<SessionModuleDto> getSessionLibrary() {
        return moduleRepository.findAll().stream()
                .map(m -> new SessionModuleDto(m.getId(), m.getName(), m.getDescription(),
                        cbtSessionRepository.findByModuleIdOrderByOrderIndexAsc(m.getId()).stream()
                                .map(s -> new SessionSummaryDto(s.getId(), s.getTitle(), s.getDescription(), s.getDurationMinutes(), s.getOrderIndex()))
                                .toList()))
                .toList();
    }

    @Override @Transactional(readOnly = true)
    public SessionDetail getSessionDetails(UUID sessionId) {
        CbtSession s = cbtSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));
        return new SessionDetail(s.getId(), s.getTitle(), s.getDescription(), s.getDurationMinutes(),
                s.getObjectives(), s.getModalities() == null ? List.of() : s.getModalities().stream().map(Enum::name).toList());
    }

    @Override @Transactional
    public ActiveSession startSession(UUID userId, UUID cbtSessionId, StartSessionRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CbtSession cbt = cbtSessionRepository.findById(cbtSessionId).orElseThrow(() -> new ResourceNotFoundException("CBT session not found"));
        userSessionRepository.findFirstByUserIdAndStatusOrderByStartedAtDesc(userId, UserSession.SessionStatus.IN_PROGRESS)
                .ifPresent(a -> { a.setStatus(UserSession.SessionStatus.EARLY_EXIT); a.setEndedAt(LocalDateTime.now()); userSessionRepository.save(a); });
        UserSession us = new UserSession();
        us.setUser(user); us.setCbtSession(cbt);
        us.setStatus(UserSession.SessionStatus.IN_PROGRESS);
        us.setStartedAt(LocalDateTime.now());
        if (req != null) us.setMoodBefore(req.moodBefore());
        us = userSessionRepository.save(us);
        log.info("Started session {} for user {}", cbtSessionId, userId);
        return new ActiveSession(us.getId(), cbtSessionId, cbt.getTitle(), us.getStartedAt(), "IN_PROGRESS");
    }

    @Override @Transactional
    public ChatResponse chat(UUID userSessionId, UUID userId, String message) {
        UserSession us = userSessionRepository.findById(userSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        if (us.getStatus() != UserSession.SessionStatus.IN_PROGRESS)
            throw new BadRequestException("Session is not active");
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserSession(us); userMsg.setRole(ChatMessage.MessageRole.USER); userMsg.setContent(message);
        chatMessageRepository.save(userMsg);
        String reply = "Thank you for sharing. Can you tell me more about what you're experiencing?";
        ChatMessage assistantMsg = new ChatMessage();
        assistantMsg.setUserSession(us); assistantMsg.setRole(ChatMessage.MessageRole.ASSISTANT); assistantMsg.setContent(reply);
        chatMessageRepository.save(assistantMsg);
        return new ChatResponse(reply, "ASSISTANT", assistantMsg.getTimestamp(), false);
    }

    @Override @Transactional
    public SessionSummary endSession(UUID userSessionId, EndSessionRequest req) {
        UserSession us = userSessionRepository.findById(userSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        us.setStatus(UserSession.SessionStatus.COMPLETED); us.setEndedAt(LocalDateTime.now());
        if (req != null && req.moodAfter() != null) us.setMoodAfter(req.moodAfter());
        userSessionRepository.save(us);
        Integer before = us.getMoodBefore(); Integer after = us.getMoodAfter();
        log.info("Ended session {}", userSessionId);
        return new SessionSummary(userSessionId,
                us.getCbtSession() != null ? us.getCbtSession().getTitle() : "Session",
                us.getStartedAt(), us.getEndedAt(), UserSession.SessionStatus.COMPLETED,
                before, after, (before != null && after != null) ? after - before : null,
                "Great work completing this session!");
    }

    @Override @Transactional(readOnly = true)
    public List<SessionHistoryEntry> getSessionHistory(UUID userId) {
        return userSessionRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(us -> new SessionHistoryEntry(us.getId(),
                        us.getCbtSession() != null ? us.getCbtSession().getId() : null,
                        us.getCbtSession() != null ? us.getCbtSession().getTitle() : "Unknown",
                        us.getStartedAt(), us.getEndedAt(), us.getStatus(), us.getMoodBefore(), us.getMoodAfter()))
                .toList();
    }
}
