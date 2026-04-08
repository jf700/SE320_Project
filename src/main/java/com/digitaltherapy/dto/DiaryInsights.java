package com.digitaltherapy.dto;

import java.util.List;
import java.util.Map;

public class DiaryInsights {

    private List<String> commonDistortions;
    private Map<String, Integer> emotionFrequency;
    private double averageMoodImprovement;
    private List<String> patterns;
    private List<String> recommendations;

    public DiaryInsights() {}


    public List<String> getCommonDistortions() { return commonDistortions; }
    public void setCommonDistortions(List<String> commonDistortions) { this.commonDistortions = commonDistortions; }

    public Map<String, Integer> getEmotionFrequency() { return emotionFrequency; }
    public void setEmotionFrequency(Map<String, Integer> emotionFrequency) { this.emotionFrequency = emotionFrequency; }

    public double getAverageMoodImprovement() { return averageMoodImprovement; }
    public void setAverageMoodImprovement(double averageMoodImprovement) { this.averageMoodImprovement = averageMoodImprovement; }

    public List<String> getPatterns() { return patterns; }
    public void setPatterns(List<String> patterns) { this.patterns = patterns; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
