package com.digitaltherapy.controller;

import com.digitaltherapy.dto.request.DiaryRequests.*;
import com.digitaltherapy.dto.response.DiaryResponses.*;
import com.digitaltherapy.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/diary")
@Tag(name = "Thought Diary")
public class DiaryController {
    private final DiaryService diaryService;
    public DiaryController(DiaryService diaryService) { this.diaryService = diaryService; }

    @GetMapping("/entries")
    @Operation(summary = "List diary entries")
    public ResponseEntity<Page<DiaryEntrySummary>> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return ResponseEntity.ok(diaryService.getEntries(SecurityUtils.currentUserId(), PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PostMapping("/entries")
    @Operation(summary = "Create diary entry")
    public ResponseEntity<DiaryEntryResponse> create(@Valid @RequestBody DiaryEntryCreate req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diaryService.createEntry(SecurityUtils.currentUserId(), req));
    }

    @GetMapping("/entries/{entryId}")
    @Operation(summary = "Get entry detail")
    public ResponseEntity<DiaryEntryResponse> get(@PathVariable UUID entryId) { return ResponseEntity.ok(diaryService.getEntryDetail(entryId)); }

    @DeleteMapping("/entries/{entryId}")
    @Operation(summary = "Delete entry")
    public ResponseEntity<Void> delete(@PathVariable UUID entryId) { diaryService.deleteEntry(entryId); return ResponseEntity.noContent().build(); }

    @PostMapping("/distortions/suggest")
    @Operation(summary = "Suggest distortions")
    public ResponseEntity<List<DistortionSuggestion>> suggest(@Valid @RequestBody DistortionSuggestRequest req) {
        return ResponseEntity.ok(diaryService.suggestDistortions(req.thought()));
    }

    @GetMapping("/insights")
    @Operation(summary = "Get insights")
    public ResponseEntity<DiaryInsights> insights() { return ResponseEntity.ok(diaryService.getInsights(SecurityUtils.currentUserId())); }
}
