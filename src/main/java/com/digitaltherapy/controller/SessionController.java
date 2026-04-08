package com.digitaltherapy.controller;

import com.digitaltherapy.dto.SessionRequest;
import com.digitaltherapy.entity.SessionModule;
import com.digitaltherapy.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    /*
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody SessionRequest request) {
        return ResponseEntity.ok(sessionService.createSession(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Session>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(sessionService.getUserSessions(userId));
    }

     */
}