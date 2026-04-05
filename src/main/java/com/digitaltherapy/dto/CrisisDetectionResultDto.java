package com.digitaltherapy.dto;

import java.util.List;

public class CrisisDetectionResultDto {

    private String riskLevel; // none, low, medium, high, critical
    private List<String> keywordsDetected;
    private String recommendedAction; // none, show_resources, show_crisis_hub, immediate_intervention
    private String reasoning;

    public CrisisDetectionResultDto() {}

    public CrisisDetectionResultDto(String riskLevel, List<String> keywordsDetected,
                                     String recommendedAction, String reasoning) {
        this.riskLevel = riskLevel;
        this.keywordsDetected = keywordsDetected;
        this.recommendedAction = recommendedAction;
        this.reasoning = reasoning;
    }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public List<String> getKeywordsDetected() { return keywordsDetected; }
    public void setKeywordsDetected(List<String> keywordsDetected) { this.keywordsDetected = keywordsDetected; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
}
