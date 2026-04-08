package com.digitaltherapy.controller;

import com.digitaltherapy.dto.CrisisRequest;
//import com.digitaltherapy.entity.CrisisRecord;
import com.digitaltherapy.service.CrisisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crisis")
@RequiredArgsConstructor
public class CrisisController {

    /*
    private final CrisisService crisisService;

    @PostMapping
    public ResponseEntity<CrisisRecord> logCrisis(@RequestBody CrisisRequest request) {
        return ResponseEntity.ok(crisisService.logCrisis(request));
    }

     */
}