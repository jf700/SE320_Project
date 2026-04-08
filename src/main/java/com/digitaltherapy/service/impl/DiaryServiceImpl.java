package com.digitaltherapy.service.impl;

import com.digitaltherapy.dto.DiaryEntryDetail;
import com.digitaltherapy.dto.DiaryEntryResponse;
import com.digitaltherapy.dto.DiaryEntryCreate;
import com.digitaltherapy.dto.DiaryEntrySummary;
import com.digitaltherapy.dto.DiaryInsights;
import com.digitaltherapy.dto.DistortionSuggestion;
import com.digitaltherapy.entity.DiaryEntry;
import com.digitaltherapy.entity.User;
import com.digitaltherapy.entity.CognitiveDistortion;
import com.digitaltherapy.repository.CognitiveDistortionRepository;
import com.digitaltherapy.repository.DiaryEntryRepository;
import com.digitaltherapy.repository.UserRepository;
import com.digitaltherapy.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryServiceImpl implements DiaryService {

    private final DiaryEntryRepository diaryRepo;
    private final UserRepository userRepo;
    private final CognitiveDistortionRepository distortionRepo;

    // 1. CREATE ENTRY
    @Override
    public DiaryEntryResponse createEntry(UUID userId, DiaryEntryCreate request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DiaryEntry entry = new DiaryEntry();
        entry.setUser(user);
        entry.setSituation(request.getSituation());
        entry.setAutomaticThought(request.getAutomaticThought());
        entry.setEmotions(request.getEmotions());
        entry.setAlternativeThought(request.getAlternativeThought());
        entry.setMoodBefore(request.getMoodBefore());
        entry.setMoodAfter(request.getMoodAfter());
        entry.setBeliefRatingBefore(request.getBeliefRatingBefore());
        entry.setBeliefRatingAfter(request.getBeliefRatingAfter());

        // distortions (optional lookup)
        if (request.getDistortionIds() != null) {
            List<CognitiveDistortion> distortions =
                    distortionRepo.findAllById(request.getDistortionIds());
            entry.setDistortions(distortions);
        }

        DiaryEntry saved = diaryRepo.save(entry);

        return mapToResponse(saved);
    }

    @Override
    public Page<DiaryEntrySummary> getEntries(UUID userId, Pageable pageable) {

        return diaryRepo
                .findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToSummary);
    }

    @Transactional
    @Override
    public DiaryEntryDetail getEntryDetail(UUID entryId) {

        DiaryEntry entry = diaryRepo.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        entry.getEmotions().size();
        entry.getDistortions().size();
        return mapToDetail(entry);
    }

    // 4. SOFT DELETE
    @Override
    public void deleteEntry(UUID entryId) {

        DiaryEntry entry = diaryRepo.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        entry.setDeleted(true);
        diaryRepo.save(entry);
    }

    @Override
    public List<DistortionSuggestion> suggestDistortions(String thought) {

        List<DistortionSuggestion> results = new ArrayList<>();

        String lower = thought.toLowerCase();

        if (lower.contains("always") || lower.contains("never")) {
            results.add(new DistortionSuggestion(
                    "ALL_OR_NOTHING",
                    0.85,
                    "Uses absolute terms like always/never"
            ));
        }

        if (lower.contains("everything is ruined") || lower.contains("disaster")) {
            results.add(new DistortionSuggestion(
                    "CATASTROPHIZING",
                    0.9,
                    "Assumes worst-case outcome is inevitable"
            ));
        }

        if (lower.contains("nobody") || lower.contains("everyone")) {
            results.add(new DistortionSuggestion(
                    "OVERGENERALIZATION",
                    0.8,
                    "Applies broad conclusions to all situations"
            ));
        }

        return results;
    }

    @Override
    public DiaryInsights getInsights(UUID userId) {

        List<DiaryEntry> entries = diaryRepo
                .findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .getContent();

        double avgImprovement = entries.stream()
                .filter(e -> e.getMoodBefore() != null && e.getMoodAfter() != null)
                .mapToInt(e -> e.getMoodAfter() - e.getMoodBefore())
                .average()
                .orElse(0.0);

        DiaryInsights insights = new DiaryInsights();
        insights.setAverageMoodImprovement(avgImprovement);

        return insights;
    }


    private DiaryEntryResponse mapToResponse(DiaryEntry e) {
        return new DiaryEntryResponse(
                e.getId(),
                e.getSituation(),
                e.getAutomaticThought(),
                e.getMoodBefore(),
                e.getMoodAfter(),
                e.getCreatedAt()
        );
    }

    private DiaryEntrySummary mapToSummary(DiaryEntry e) {
        return new DiaryEntrySummary(
                e.getId(),
                e.getSituation(),
                e.getMoodBefore(),
                e.getMoodAfter(),
                e.getCreatedAt()
        );
    }

    private DiaryEntryDetail mapToDetail(DiaryEntry e) {
        return new DiaryEntryDetail(
                e.getId(),
                e.getSituation(),
                e.getAutomaticThought(),
                e.getAlternativeThought(),
                e.getEmotions(),
                e.getDistortions(),
                e.getMoodBefore(),
                e.getMoodAfter(),
                e.getBeliefRatingBefore(),
                e.getBeliefRatingAfter(),
                e.getCreatedAt()
        );
    }
}