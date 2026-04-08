package com.digitaltherapy.dto;

import com.digitaltherapy.entity.CognitiveDistortion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DiaryEntryDetail {

    private UUID id;
    private String situation;
    private String automaticThought;
    private String alternativeThought;
    private List<String> emotions;
    private List<CognitiveDistortion> distortions;

    private Integer moodBefore;
    private Integer moodAfter;
    private Integer beliefRatingBefore;
    private Integer beliefRatingAfter;

    private LocalDateTime createdAt;

    public DiaryEntryDetail() {}

    public DiaryEntryDetail(UUID id, String situation, String automaticThought,
                            String alternativeThought, List<String> emotions,
                            List<CognitiveDistortion> distortions,
                            Integer moodBefore, Integer moodAfter,
                            Integer beliefRatingBefore, Integer beliefRatingAfter,
                            LocalDateTime createdAt) {
        this.id = id;
        this.situation = situation;
        this.automaticThought = automaticThought;
        this.alternativeThought = alternativeThought;
        this.emotions = emotions;
        this.distortions = distortions;
        this.moodBefore = moodBefore;
        this.moodAfter = moodAfter;
        this.beliefRatingBefore = beliefRatingBefore;
        this.beliefRatingAfter = beliefRatingAfter;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSituation() { return situation; }
    public void setSituation(String situation) { this.situation = situation; }

    public String getAutomaticThought() { return automaticThought; }
    public void setAutomaticThought(String automaticThought) { this.automaticThought = automaticThought; }

    public String getAlternativeThought() { return alternativeThought; }
    public void setAlternativeThought(String alternativeThought) { this.alternativeThought = alternativeThought; }

    public List<String> getEmotions() { return emotions; }
    public void setEmotions(List<String> emotions) { this.emotions = emotions; }

    public List<CognitiveDistortion> getDistortions() { return distortions; }
    public void setDistortions(List<CognitiveDistortion> distortions) { this.distortions = distortions; }

    public Integer getMoodBefore() { return moodBefore; }
    public void setMoodBefore(Integer moodBefore) { this.moodBefore = moodBefore; }

    public Integer getMoodAfter() { return moodAfter; }
    public void setMoodAfter(Integer moodAfter) { this.moodAfter = moodAfter; }

    public Integer getBeliefRatingBefore() { return beliefRatingBefore; }
    public void setBeliefRatingBefore(Integer beliefRatingBefore) { this.beliefRatingBefore = beliefRatingBefore; }

    public Integer getBeliefRatingAfter() { return beliefRatingAfter; }
    public void setBeliefRatingAfter(Integer beliefRatingAfter) { this.beliefRatingAfter = beliefRatingAfter; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}