package com.digitaltherapy.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public final class DiaryRequests {

    private DiaryRequests() {}

    public record DiaryEntryCreate(
        @NotBlank(message = "Situation is required")
        @Size(max = 2000)
        String situation,

        @NotBlank(message = "Automatic thought is required")
        @Size(max = 2000)
        String automaticThought,

        List<EmotionInput> emotions,

        List<String> distortionIds,

        @Size(max = 2000)
        String alternativeThought,

        @Min(1) @Max(10)
        Integer moodBefore,

        @Min(1) @Max(10)
        Integer moodAfter,

        @Min(0) @Max(100)
        Integer beliefRatingBefore,

        @Min(0) @Max(100)
        Integer beliefRatingAfter
    ) {}

    public record EmotionInput(
        @NotBlank String emotion,
        @Min(0) @Max(100) Integer intensity
    ) {}

    public record DistortionSuggestRequest(
        @NotBlank(message = "Thought is required")
        @Size(max = 2000)
        String thought
    ) {}
}
