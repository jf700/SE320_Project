package com.digitaltherapy.dto.response;

import java.util.List;

public final class CrisisResponses {

    private CrisisResponses() {}

    public record CrisisHub(
        String message,
        List<CopingStrategy> quickStrategies,
        List<EmergencyResource> emergencyResources,
        String hotlineNumber
    ) {}

    public record CopingStrategy(
        String id,
        String title,
        String description,
        String category,
        int durationMinutes
    ) {}

    public record EmergencyResource(
        String name,
        String phone,
        String url,
        String description,
        boolean available24x7
    ) {}

    public record CrisisDetectionResult(
        RiskLevel riskLevel,
        List<String> keywordsDetected,
        RecommendedAction recommendedAction,
        String reasoning
    ) {}

    public enum RiskLevel { none, low, medium, high, critical }
    public enum RecommendedAction { none, show_resources, show_crisis_hub, immediate_intervention }

    public record SafetyPlan(
        List<String> warningSignals,
        List<String> copingStrategies,
        List<String> socialSupports,
        List<String> professionalContacts,
        List<String> environmentSafety,
        String reasonsForLiving
    ) {}
}
