package com.digitaltherapy.service;

import com.digitaltherapy.dto.request.CrisisRequests.*;
import com.digitaltherapy.dto.response.CrisisResponses.*;
import com.digitaltherapy.service.impl.CrisisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class CrisisServiceImplTest {

    private CrisisServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CrisisServiceImpl();
    }

    // ── getCrisisHub ──────────────────────────────────────────────────────────

    @Test
    void getCrisisHub_returnsNonNullHub() {
        CrisisHub hub = service.getCrisisHub();
        assertThat(hub).isNotNull();
        assertThat(hub.message()).isNotBlank();
        assertThat(hub.hotlineNumber()).isEqualTo("988");
        assertThat(hub.quickStrategies()).isNotEmpty();
        assertThat(hub.emergencyResources()).isNotEmpty();
    }

    // ── getCopingStrategies ───────────────────────────────────────────────────

    @Test
    void getCopingStrategies_returnsMultipleStrategies() {
        List<CopingStrategy> strategies = service.getCopingStrategies();
        assertThat(strategies).hasSizeGreaterThanOrEqualTo(4);
        strategies.forEach(s -> {
            assertThat(s.id()).isNotBlank();
            assertThat(s.title()).isNotBlank();
            assertThat(s.description()).isNotBlank();
            assertThat(s.durationMinutes()).isPositive();
        });
    }

    // ── detectCrisis ──────────────────────────────────────────────────────────

    @Test
    void detectCrisis_returnsNone_forSafeText() {
        CrisisDetectionResult result = service.detectCrisis("I had a productive day at work.");
        assertThat(result.riskLevel()).isEqualTo(RiskLevel.none);
        assertThat(result.recommendedAction()).isEqualTo(RecommendedAction.none);
        assertThat(result.keywordsDetected()).isEmpty();
    }

    @Test
    void detectCrisis_returnsLow_forMildDistress() {
        CrisisDetectionResult result = service.detectCrisis("I feel completely hopeless and overwhelmed.");
        assertThat(result.riskLevel()).isIn(RiskLevel.low, RiskLevel.medium);
    }

    @Test
    void detectCrisis_returnsCritical_forSuicidalKeyword() {
        CrisisDetectionResult result = service.detectCrisis("I want to die and I've been thinking about suicide.");
        assertThat(result.riskLevel()).isEqualTo(RiskLevel.critical);
        assertThat(result.recommendedAction()).isEqualTo(RecommendedAction.immediate_intervention);
        assertThat(result.keywordsDetected()).isNotEmpty();
    }

    @Test
    void detectCrisis_returnsHigh_forMultipleKeywords() {
        CrisisDetectionResult result = service.detectCrisis(
                "I want to hurt myself and I can't go on like this.");
        assertThat(result.riskLevel()).isIn(RiskLevel.high, RiskLevel.critical);
        assertThat(result.recommendedAction()).isIn(
                RecommendedAction.show_crisis_hub, RecommendedAction.immediate_intervention);
    }

    @Test
    void detectCrisis_isCaseInsensitive() {
        CrisisDetectionResult result = service.detectCrisis("I WANT TO DIE.");
        assertThat(result.riskLevel()).isEqualTo(RiskLevel.critical);
    }

    @Test
    void detectCrisis_reasoning_isNotBlank() {
        CrisisDetectionResult result = service.detectCrisis("Everything is fine.");
        assertThat(result.reasoning()).isNotBlank();
    }

    // ── getSafetyPlan ─────────────────────────────────────────────────────────

    @Test
    void getSafetyPlan_returnsDefaultPlan_forNewUser() {
        SafetyPlan plan = service.getSafetyPlan(UUID.randomUUID());
        assertThat(plan).isNotNull();
        assertThat(plan.warningSignals()).isNotEmpty();
        assertThat(plan.copingStrategies()).isNotEmpty();
    }

    // ── updateSafetyPlan ──────────────────────────────────────────────────────

    @Test
    void updateSafetyPlan_persistsAndReturnsPlan() {
        UUID userId = UUID.randomUUID();
        SafetyPlanUpdate update = new SafetyPlanUpdate(
                List.of("Feeling withdrawn"),
                List.of("Call a friend"),
                List.of("My sister"),
                List.of("Dr. Smith: 555-0100"),
                List.of("Remove sharp objects"),
                "My children, my purpose"
        );

        SafetyPlan result = service.updateSafetyPlan(userId, update);

        assertThat(result.warningSignals()).containsExactly("Feeling withdrawn");
        assertThat(result.copingStrategies()).containsExactly("Call a friend");
        assertThat(result.reasonsForLiving()).isEqualTo("My children, my purpose");

        // Verify subsequent getSafetyPlan returns the updated plan
        SafetyPlan fetched = service.getSafetyPlan(userId);
        assertThat(fetched.reasonsForLiving()).isEqualTo("My children, my purpose");
    }

    @Test
    void updateSafetyPlan_handlesNullLists_gracefully() {
        UUID userId = UUID.randomUUID();
        SafetyPlanUpdate update = new SafetyPlanUpdate(null, null, null, null, null, "Hope");
        SafetyPlan result = service.updateSafetyPlan(userId, update);
        assertThat(result.warningSignals()).isEmpty();
        assertThat(result.reasonsForLiving()).isEqualTo("Hope");
    }
}
