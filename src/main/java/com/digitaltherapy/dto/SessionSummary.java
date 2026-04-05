package com.digitaltherapy.dto;

import java.util.List;
import java.util.UUID;

public class SessionSummary {

    private UUID sessionId;
    private String summary;
    private List<String> keyTopics;
    private List<String> techniquesUsed;
    private List<String> distortionsIdentified;
    private String moodProgression;
    private List<String> homeworkSuggestions;

    public SessionSummary() {}

    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getKeyTopics() { return keyTopics; }
    public void setKeyTopics(List<String> keyTopics) { this.keyTopics = keyTopics; }

    public List<String> getTechniquesUsed() { return techniquesUsed; }
    public void setTechniquesUsed(List<String> techniquesUsed) { this.techniquesUsed = techniquesUsed; }

    public List<String> getDistortionsIdentified() { return distortionsIdentified; }
    public void setDistortionsIdentified(List<String> distortionsIdentified) { this.distortionsIdentified = distortionsIdentified; }

    public String getMoodProgression() { return moodProgression; }
    public void setMoodProgression(String moodProgression) { this.moodProgression = moodProgression; }

    public List<String> getHomeworkSuggestions() { return homeworkSuggestions; }
    public void setHomeworkSuggestions(List<String> homeworkSuggestions) { this.homeworkSuggestions = homeworkSuggestions; }
}
