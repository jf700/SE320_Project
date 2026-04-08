package com.digitaltherapy.service.impl;

import com.digitaltherapy.dto.request.CrisisRequests.*;
import com.digitaltherapy.dto.response.CrisisResponses.*;
import com.digitaltherapy.service.CrisisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CrisisServiceImpl implements CrisisService {
    private static final Logger log = LoggerFactory.getLogger(CrisisServiceImpl.class);
    private static final Set<String> CRISIS_KEYWORDS = Set.of("suicide","kill myself","end it all","no reason to live","better off dead","can't go on","want to die","hurt myself","hopeless","worthless");
    private final Map<UUID, SafetyPlan> safetyPlans = new ConcurrentHashMap<>();

    @Override
    public CrisisHub getCrisisHub() {
        return new CrisisHub("You are not alone. Help is available right now.",
                List.of(new CopingStrategy("box-breathing","Box Breathing","Inhale 4s, hold 4s, exhale 4s, hold 4s.","breathing",5),
                        new CopingStrategy("grounding","5-4-3-2-1 Grounding","Name 5 things you see, 4 you hear, 3 you touch.","mindfulness",5)),
                emergencyResources(), "988");
    }

    @Override
    public List<CopingStrategy> getCopingStrategies() {
        return List.of(
            new CopingStrategy("box-breathing","Box Breathing","Inhale 4s, hold 4s, exhale 4s, hold 4s.","breathing",5),
            new CopingStrategy("pmr","Progressive Muscle Relaxation","Tense and release each muscle group.","physical",15),
            new CopingStrategy("grounding","5-4-3-2-1 Grounding","Engage your five senses.","mindfulness",5),
            new CopingStrategy("walk","Take a Walk","10 minutes outside reduces stress hormones.","behavioral",10),
            new CopingStrategy("reach-out","Connect with Someone","Call a trusted person.","social",5));
    }

    @Override
    public CrisisDetectionResult detectCrisis(String text) {
        String lower = text.toLowerCase();
        List<String> found = CRISIS_KEYWORDS.stream().filter(lower::contains).toList();
        RiskLevel risk; RecommendedAction action;
        if (found.size() >= 3 || lower.contains("suicide") || lower.contains("want to die")) { risk = RiskLevel.critical; action = RecommendedAction.immediate_intervention; }
        else if (found.size() == 2) { risk = RiskLevel.high; action = RecommendedAction.show_crisis_hub; }
        else if (found.size() == 1) { risk = RiskLevel.medium; action = RecommendedAction.show_resources; }
        else if (lower.contains("overwhelmed") || lower.contains("burnout")) { risk = RiskLevel.low; action = RecommendedAction.show_resources; }
        else { risk = RiskLevel.none; action = RecommendedAction.none; }
        log.info("Crisis detection: risk={}", risk);
        return new CrisisDetectionResult(risk, found, action, found.isEmpty() ? "No crisis indicators detected." : "Detected " + found.size() + " indicator(s).");
    }

    @Override
    public SafetyPlan getSafetyPlan(UUID userId) { return safetyPlans.getOrDefault(userId, defaultPlan()); }

    @Override
    public SafetyPlan updateSafetyPlan(UUID userId, SafetyPlanUpdate req) {
        SafetyPlan plan = new SafetyPlan(
            req.warningSignals() != null ? req.warningSignals() : List.of(),
            req.copingStrategies() != null ? req.copingStrategies() : List.of(),
            req.socialSupports() != null ? req.socialSupports() : List.of(),
            req.professionalContacts() != null ? req.professionalContacts() : List.of(),
            req.environmentSafety() != null ? req.environmentSafety() : List.of(),
            req.reasonsForLiving());
        safetyPlans.put(userId, plan); return plan;
    }

    private SafetyPlan defaultPlan() {
        return new SafetyPlan(List.of("Feeling overwhelmed","Withdrawing from others"),
                List.of("Box breathing","5-4-3-2-1 grounding"), List.of("A trusted friend"),
                List.of("988 Suicide & Crisis Lifeline"), List.of("Remove access to harmful items"), "My reasons for living.");
    }

    private List<EmergencyResource> emergencyResources() {
        return List.of(
            new EmergencyResource("988 Suicide & Crisis Lifeline","988","https://988lifeline.org","Free 24/7 crisis support.",true),
            new EmergencyResource("Crisis Text Line","Text HOME to 741741","https://www.crisistextline.org","Text-based counseling.",true),
            new EmergencyResource("Emergency Services","911",null,"Life-threatening emergencies.",true));
    }
}
