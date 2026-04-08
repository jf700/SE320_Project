package com.digitaltherapy.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DiaryRequest {

    private UUID userId;

    private String situation;

    private String automaticThought;

    private List<String> emotions;

    private List<Long> distortionIds; // optional (only if you map distortions later)

    private String alternativeThought;

    private Integer moodBefore;

    private Integer moodAfter;

    private Integer beliefRatingBefore;

    private Integer beliefRatingAfter;
}