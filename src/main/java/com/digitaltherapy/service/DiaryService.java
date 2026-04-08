package com.digitaltherapy.service;

import com.digitaltherapy.dto.*;
import com.digitaltherapy.entity.DiaryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DiaryService {

    DiaryEntryResponse createEntry(UUID userId, DiaryEntryCreate request);
    Page<DiaryEntrySummary> getEntries(UUID userId, Pageable pageable);
    DiaryEntryDetail getEntryDetail(UUID entryId);
    void deleteEntry(UUID entryId);
    List<DistortionSuggestion> suggestDistortions(String thought);
    DiaryInsights getInsights(UUID userId);
}