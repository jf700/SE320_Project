package com.digitaltherapy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CrisisRequests {

    private CrisisRequests() {}

    public record CrisisDetectRequest(
        @NotBlank(message = "Text is required")
        @Size(max = 5000)
        String text
    ) {}

    public record SafetyPlanUpdate(
        java.util.List<String> warningSignals,
        java.util.List<String> copingStrategies,
        java.util.List<String> socialSupports,
        java.util.List<String> professionalContacts,
        java.util.List<String> environmentSafety,
        String reasonsForLiving
    ) {}
}
