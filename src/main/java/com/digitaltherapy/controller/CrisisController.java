package com.digitaltherapy.controller;

import com.digitaltherapy.dto.request.CrisisRequests.*;
import com.digitaltherapy.dto.response.CrisisResponses.*;
import com.digitaltherapy.service.CrisisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/crisis")
@Tag(name = "Crisis Support")
public class CrisisController {
    private final CrisisService crisisService;
    public CrisisController(CrisisService crisisService) { this.crisisService = crisisService; }

    @GetMapping @Operation(summary = "Crisis hub")
    public ResponseEntity<CrisisHub> hub() { return ResponseEntity.ok(crisisService.getCrisisHub()); }

    @GetMapping("/coping-strategies") @Operation(summary = "Coping strategies")
    public ResponseEntity<List<CopingStrategy>> coping() { return ResponseEntity.ok(crisisService.getCopingStrategies()); }

    @PostMapping("/detect") @Operation(summary = "Detect crisis")
    public ResponseEntity<CrisisDetectionResult> detect(@Valid @RequestBody CrisisDetectRequest req) {
        return ResponseEntity.ok(crisisService.detectCrisis(req.text()));
    }

    @GetMapping("/safety-plan") @Operation(summary = "Get safety plan")
    public ResponseEntity<SafetyPlan> getPlan() { return ResponseEntity.ok(crisisService.getSafetyPlan(SecurityUtils.currentUserId())); }

    @PutMapping("/safety-plan") @Operation(summary = "Update safety plan")
    public ResponseEntity<SafetyPlan> updatePlan(@Valid @RequestBody SafetyPlanUpdate req) {
        return ResponseEntity.ok(crisisService.updateSafetyPlan(SecurityUtils.currentUserId(), req));
    }
}
