package com.digitaltherapy.service.impl;

import com.digitaltherapy.dto.request.DiaryRequests.*;
import com.digitaltherapy.dto.response.DiaryResponses.*;
import com.digitaltherapy.entity.*;
import com.digitaltherapy.exception.ResourceNotFoundException;
import com.digitaltherapy.repository.*;
import com.digitaltherapy.service.DiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class DiaryServiceImpl implements DiaryService {
    private static final Logger log = LoggerFactory.getLogger(DiaryServiceImpl.class);
    private final DiaryEntryRepository diaryEntryRepository;
    private final UserRepository userRepository;
    private final CognitiveDistortionRepository distortionRepository;

    public DiaryServiceImpl(DiaryEntryRepository diaryEntryRepository, UserRepository userRepository,
                            CognitiveDistortionRepository distortionRepository) {
        this.diaryEntryRepository = diaryEntryRepository; this.userRepository = userRepository;
        this.distortionRepository = distortionRepository;
    }

    @Override @Transactional
    public DiaryEntryResponse createEntry(UUID userId, DiaryEntryCreate req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        List<CognitiveDistortion> distortions = req.distortionIds() == null ? List.of() : distortionRepository.findAllById(req.distortionIds());
        DiaryEntry entry = new DiaryEntry();
        entry.setUser(user); entry.setSituation(req.situation()); entry.setAutomaticThought(req.automaticThought());
        entry.setAlternativeThought(req.alternativeThought()); entry.setMoodBefore(req.moodBefore()); entry.setMoodAfter(req.moodAfter());
        entry.setBeliefRatingBefore(req.beliefRatingBefore()); entry.setBeliefRatingAfter(req.beliefRatingAfter());
        entry.setDistortions(new ArrayList<>(distortions));
        if (req.emotions() != null) entry.setEmotions(req.emotions().stream().map(e -> e.emotion() + ":" + e.intensity()).toList());
        entry = diaryEntryRepository.save(entry);
        log.info("Created diary entry for user {}", userId);
        return toResponse(entry);
    }

    @Override @Transactional(readOnly = true)
    public Page<DiaryEntrySummary> getEntries(UUID userId, Pageable pageable) {
        return diaryEntryRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(e -> new DiaryEntrySummary(e.getId(), e.getSituation(), e.getAutomaticThought(),
                        e.getMoodBefore(), e.getMoodAfter(),
                        e.getDistortions() == null ? 0 : e.getDistortions().size(), e.getCreatedAt()));
    }

    @Override @Transactional(readOnly = true)
    public DiaryEntryResponse getEntryDetail(UUID entryId) {
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Diary entry not found: " + entryId));
        return toResponse(entry);
    }

    @Override @Transactional
    public void deleteEntry(UUID entryId) {
        DiaryEntry entry = diaryEntryRepository.findById(entryId).orElseThrow(() -> new ResourceNotFoundException("Not found: " + entryId));
        entry.setDeleted(true); diaryEntryRepository.save(entry);
    }

    @Override @Transactional(readOnly = true)
    public List<DistortionSuggestion> suggestDistortions(String thought) {
        List<DistortionSuggestion> suggestions = new ArrayList<>();
        String lower = thought.toLowerCase();
        Map<String, String[]> keywords = Map.of(
                "all-or-nothing", new String[]{"always", "never", "everyone", "nobody"},
                "catastrophizing", new String[]{"disaster", "terrible", "awful", "worst"},
                "mind-reading",   new String[]{"they think", "must think", "they hate"},
                "overgeneralization", new String[]{"always fail", "never works", "every time"});
        for (Map.Entry<String, String[]> e : keywords.entrySet()) {
            double confidence = Arrays.stream(e.getValue()).filter(lower::contains).count() * 0.3;
            if (confidence > 0) distortionRepository.findById(e.getKey()).ifPresent(d ->
                    suggestions.add(new DistortionSuggestion(d.getId(), d.getName(), Math.min(confidence, 0.95), "Pattern detected")));
        }
        return suggestions;
    }

    @Override @Transactional(readOnly = true)
    public DiaryInsights getInsights(UUID userId) {
        List<DiaryEntry> entries = diaryEntryRepository.findByUserIdAndDeletedFalse(userId);
        Double avgMood = diaryEntryRepository.calculateAverageMoodImprovement(userId);
        List<String> patterns = new ArrayList<>();
        if (entries.size() > 5) patterns.add("You've been consistently tracking your thoughts!");
        if (avgMood != null && avgMood > 0) patterns.add("Your mood tends to improve after completing entries.");
        return new DiaryInsights(entries.size(), avgMood != null ? avgMood : 0.0, List.of(), patterns,
                "Keep exploring your thought patterns — awareness is the first step to change.");
    }

    private DiaryEntryResponse toResponse(DiaryEntry e) {
        List<DistortionDto> distortions = e.getDistortions() == null ? List.of() :
                e.getDistortions().stream().map(d -> new DistortionDto(d.getId(), d.getName(), d.getDescription())).toList();
        return new DiaryEntryResponse(e.getId(), e.getSituation(), e.getAutomaticThought(),
                List.of(), distortions, e.getAlternativeThought(),
                e.getMoodBefore(), e.getMoodAfter(), e.getBeliefRatingBefore(), e.getBeliefRatingAfter(), e.getCreatedAt());
    }
}
