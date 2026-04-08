package com.digitaltherapy.controller;

import com.digitaltherapy.dto.response.ProgressResponses.*;
import com.digitaltherapy.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
@Tag(name = "Progress")
public class ProgressController {
    private final ProgressService progressService;
    public ProgressController(ProgressService progressService) { this.progressService = progressService; }

    @GetMapping("/weekly") @Operation(summary = "Weekly progress")
    public ResponseEntity<WeeklyProgress> weekly() { return ResponseEntity.ok(progressService.getWeeklyProgress(SecurityUtils.currentUserId())); }

    @GetMapping("/monthly") @Operation(summary = "Monthly trends")
    public ResponseEntity<MonthlyTrends> monthly() { return ResponseEntity.ok(progressService.getMonthlyTrends(SecurityUtils.currentUserId())); }

    @GetMapping("/burnout") @Operation(summary = "Burnout recovery")
    public ResponseEntity<BurnoutRecovery> burnout() { return ResponseEntity.ok(progressService.getBurnoutRecovery(SecurityUtils.currentUserId())); }

    @GetMapping("/achievements") @Operation(summary = "Achievements")
    public ResponseEntity<Achievements> achievements() { return ResponseEntity.ok(progressService.getAchievements(SecurityUtils.currentUserId())); }
}
