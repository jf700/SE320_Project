package com.digitaltherapy.service;

import com.digitaltherapy.dto.request.SessionRequests.*;
import com.digitaltherapy.dto.response.SessionResponses.*;

import java.util.List;
import java.util.UUID;

public interface SessionService {
    List<SessionModuleDto> getSessionLibrary();
    SessionDetail getSessionDetails(UUID sessionId);
    ActiveSession startSession(UUID userId, UUID cbtSessionId, StartSessionRequest req);
    ChatResponse chat(UUID userSessionId, UUID userId, String message);
    SessionSummary endSession(UUID userSessionId, EndSessionRequest req);
    List<SessionHistoryEntry> getSessionHistory(UUID userId);
}
