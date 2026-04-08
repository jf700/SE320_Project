package com.digitaltherapy.controller;

import com.digitaltherapy.dto.request.SessionRequests.*;
import com.digitaltherapy.dto.response.SessionResponses.*;
import com.digitaltherapy.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
@Tag(name = "Sessions")
public class SessionController {
    private final SessionService sessionService;
    public SessionController(SessionService sessionService) { this.sessionService = sessionService; }

    @GetMapping
    @Operation(summary = "Get session library")
    public ResponseEntity<List<SessionModuleDto>> getLibrary() { return ResponseEntity.ok(sessionService.getSessionLibrary()); }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session details")
    public ResponseEntity<SessionDetail> getDetails(@PathVariable UUID sessionId) { return ResponseEntity.ok(sessionService.getSessionDetails(sessionId)); }

    @PostMapping("/{sessionId}/start")
    @Operation(summary = "Start a CBT session")
    public ResponseEntity<ActiveSession> start(@PathVariable UUID sessionId, @Valid @RequestBody(required = false) StartSessionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.startSession(SecurityUtils.currentUserId(), sessionId, req));
    }

    @PostMapping("/{userSessionId}/chat")
    @Operation(summary = "Send chat message")
    public ResponseEntity<ChatResponse> chat(@PathVariable UUID userSessionId, @Valid @RequestBody ChatRequest req) {
        return ResponseEntity.ok(sessionService.chat(userSessionId, SecurityUtils.currentUserId(), req.message()));
    }

    @PostMapping("/{userSessionId}/end")
    @Operation(summary = "End session")
    public ResponseEntity<SessionSummary> end(@PathVariable UUID userSessionId, @Valid @RequestBody(required = false) EndSessionRequest req) {
        return ResponseEntity.ok(sessionService.endSession(userSessionId, req));
    }

    @GetMapping("/history")
    @Operation(summary = "Get session history")
    public ResponseEntity<List<SessionHistoryEntry>> history() { return ResponseEntity.ok(sessionService.getSessionHistory(SecurityUtils.currentUserId())); }
}
