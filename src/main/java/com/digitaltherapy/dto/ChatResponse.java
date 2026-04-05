package com.digitaltherapy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatResponse {

    private UUID sessionId;
    private String message;
    private String role;
    private LocalDateTime timestamp;
    private CrisisDetectionResultDto crisisAssessment;

    public ChatResponse() {}

    public ChatResponse(UUID sessionId, String message, String role, LocalDateTime timestamp) {
        this.sessionId = sessionId;
        this.message = message;
        this.role = role;
        this.timestamp = timestamp;
    }

    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public CrisisDetectionResultDto getCrisisAssessment() { return crisisAssessment; }
    public void setCrisisAssessment(CrisisDetectionResultDto crisisAssessment) { this.crisisAssessment = crisisAssessment; }
}
