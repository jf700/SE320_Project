package com.digitaltherapy.service;

import com.digitaltherapy.dto.request.CrisisRequests.*;
import com.digitaltherapy.dto.response.CrisisResponses.*;

import java.util.List;
import java.util.UUID;

public interface CrisisService {
    CrisisHub getCrisisHub();
    List<CopingStrategy> getCopingStrategies();
    CrisisDetectionResult detectCrisis(String text);
    SafetyPlan getSafetyPlan(UUID userId);
    SafetyPlan updateSafetyPlan(UUID userId, SafetyPlanUpdate request);
}
