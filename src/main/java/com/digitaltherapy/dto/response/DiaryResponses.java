package com.digitaltherapy.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class DiaryResponses {

    private DiaryResponses() {}

    public record DiaryEntryResponse(
        UUID id,
        String situation,
        String automaticThought,
        List<EmotionDto> emotions,
        List<DistortionDto> distortions,
        String alternativeThought,
        Integer moodBefore,
        Integer moodAfter,
        Integer beliefRatingBefore,
        Integer beliefRatingAfter,
        LocalDateTime createdAt
    ) {}

    public record DiaryEntrySummary(
        UUID id,
        String situation,
        String automaticThought,
        Integer moodBefore,
        Integer moodAfter,
        int distortionCount,
        LocalDateTime createdAt
    ) {}

    public record EmotionDto(String emotion, Integer intensity) {}

    public record DistortionDto(String id, String name, String description) {}

    public record DistortionSuggestion(
        String distortionId,
        String distortionName,
        double confidence,
        String reasoning
    ) {}

    public record DiaryInsights(
        long totalEntries,
        double averageMoodImprovement,
        List<TopDistortion> topDistortions,
        List<String> patterns,
        String aiInsight
    ) {}

    public record TopDistortion(String id, String name, long occurrences) {}
}
