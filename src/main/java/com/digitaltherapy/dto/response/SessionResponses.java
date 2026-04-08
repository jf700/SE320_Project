package com.digitaltherapy.dto.response;

import com.digitaltherapy.entity.UserSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class SessionResponses {

    private SessionResponses() {}

    public record SessionModuleDto(
        UUID id,
        String title,
        String description,
        List<SessionSummaryDto> sessions
    ) {}

    public record SessionSummaryDto(
        UUID id,
        String title,
        String description,
        Integer durationMinutes,
        Integer orderIndex
    ) {}

    public record SessionDetail(
        UUID id,
        String title,
        String description,
        Integer durationMinutes,
        List<String> objectives,
        List<String> modalities
    ) {}

    public record ActiveSession(
        UUID userSessionId,
        UUID cbtSessionId,
        String sessionTitle,
        LocalDateTime startedAt,
        String status
    ) {}

    public record ChatResponse(
        String message,
        String role,
        LocalDateTime timestamp,
        boolean crisisDetected
    ) {}

    public record SessionSummary(
        UUID sessionId,
        String title,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        UserSession.SessionStatus status,
        Integer moodBefore,
        Integer moodAfter,
        Integer moodImprovement,
        String aiSummary
    ) {}

    public record SessionHistoryEntry(
        UUID userSessionId,
        UUID cbtSessionId,
        String sessionTitle,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        UserSession.SessionStatus status,
        Integer moodBefore,
        Integer moodAfter
    ) {}
}
