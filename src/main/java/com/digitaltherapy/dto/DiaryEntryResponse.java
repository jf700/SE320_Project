package com.digitaltherapy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DiaryEntryResponse {

    private UUID id;
    private String situation;
    private String automaticThought;
    private Integer moodBefore;
    private Integer moodAfter;
    private LocalDateTime createdAt;

    public DiaryEntryResponse() {}

    public DiaryEntryResponse(UUID id, String situation, String automaticThought,
                              Integer moodBefore, Integer moodAfter,
                              LocalDateTime createdAt) {
        this.id = id;
        this.situation = situation;
        this.automaticThought = automaticThought;
        this.moodBefore = moodBefore;
        this.moodAfter = moodAfter;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSituation() { return situation; }
    public void setSituation(String situation) { this.situation = situation; }

    public String getAutomaticThought() { return automaticThought; }
    public void setAutomaticThought(String automaticThought) { this.automaticThought = automaticThought; }

    public Integer getMoodBefore() { return moodBefore; }
    public void setMoodBefore(Integer moodBefore) { this.moodBefore = moodBefore; }

    public Integer getMoodAfter() { return moodAfter; }
    public void setMoodAfter(Integer moodAfter) { this.moodAfter = moodAfter; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}