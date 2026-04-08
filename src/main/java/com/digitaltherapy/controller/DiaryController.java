package com.digitaltherapy.controller;

import com.digitaltherapy.dto.*;
import com.digitaltherapy.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    // ✅ CREATE ENTRY
    @PostMapping("/entries")
    public ResponseEntity<DiaryEntryResponse> createEntry(
            @RequestParam UUID userId,
            @RequestBody DiaryEntryCreate request
    ) {
        return ResponseEntity.ok(diaryService.createEntry(userId, request));
    }

    // ✅ LIST ENTRIES
    @GetMapping("/entries")
    public ResponseEntity<?> getEntries(
            @RequestParam UUID userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(diaryService.getEntries(userId, pageable));
    }

    // ✅ GET ENTRY DETAIL
    @GetMapping("/entries/{entryId}")
    public ResponseEntity<DiaryEntryDetail> getEntry(@PathVariable UUID entryId) {
        return ResponseEntity.ok(diaryService.getEntryDetail(entryId));
    }

    // ✅ DELETE ENTRY
    @DeleteMapping("/entries/{entryId}")
    public ResponseEntity<Void> delete(@PathVariable UUID entryId) {
        diaryService.deleteEntry(entryId);
        return ResponseEntity.noContent().build();
    }

    // 🤖 AI DISTORTION SUGGESTIONS
    @PostMapping("/distortions/suggest")
    public ResponseEntity<?> suggestDistortions(
            @RequestBody DiaryEntryCreate request
    ) {
        return ResponseEntity.ok(diaryService.suggestDistortions(request.getAutomaticThought()));
    }

    // 📊 INSIGHTS
    @GetMapping("/insights")
    public ResponseEntity<DiaryInsights> getInsights(
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(diaryService.getInsights(userId));
    }
}