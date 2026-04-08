package com.digitaltherapy.dto;

import java.util.List;
import java.util.UUID;

public class DiaryEntryCreate {

    private String situation;
    private String automaticThought;
    private List<String> emotions;
    private String alternativeThought;

    private Integer moodBefore;
    private Integer moodAfter;
    private Integer beliefRatingBefore;
    private Integer beliefRatingAfter;

    private List<String> distortionIds;

    public DiaryEntryCreate() {}

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getAutomaticThought() {
        return automaticThought;
    }

    public void setAutomaticThought(String automaticThought) {
        this.automaticThought = automaticThought;
    }

    public List<String> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<String> emotions) {
        this.emotions = emotions;
    }

    public String getAlternativeThought() {
        return alternativeThought;
    }

    public void setAlternativeThought(String alternativeThought) {
        this.alternativeThought = alternativeThought;
    }

    public Integer getMoodBefore() {
        return moodBefore;
    }

    public void setMoodBefore(Integer moodBefore) {
        this.moodBefore = moodBefore;
    }

    public Integer getMoodAfter() {
        return moodAfter;
    }

    public void setMoodAfter(Integer moodAfter) {
        this.moodAfter = moodAfter;
    }

    public Integer getBeliefRatingBefore() {
        return beliefRatingBefore;
    }

    public void setBeliefRatingBefore(Integer beliefRatingBefore) {
        this.beliefRatingBefore = beliefRatingBefore;
    }

    public Integer getBeliefRatingAfter() {
        return beliefRatingAfter;
    }

    public void setBeliefRatingAfter(Integer beliefRatingAfter) {
        this.beliefRatingAfter = beliefRatingAfter;
    }

    public List<String> getDistortionIds() {
        return distortionIds;
    }

    public void setDistortionIds(List<String> distortionIds) {
        this.distortionIds = distortionIds;
    }
}