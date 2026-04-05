package com.digitaltherapy.service.rag;

import com.digitaltherapy.dto.CrisisDetectionResultDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class CrisisDetector {

    private static final Logger log = LoggerFactory.getLogger(CrisisDetector.class);

    private static final Set<String> CRISIS_KEYWORDS = Set.of(
        "suicide", "kill myself", "end it all", "no reason to live",
        "better off dead", "can't go on", "want to die", "hurt myself"
    );

    private static final String CRISIS_DETECTION_PROMPT = """
        Analyze the following text for crisis indicators. Assess risk level and
        recommend appropriate action.

        Text: "%s"

        Evaluate for:
        - Suicidal ideation or self-harm mentions
        - Expressions of hopelessness
        - Statements about being a burden
        - Plans or intentions to harm self/others
        - Severe emotional distress

        Return ONLY a JSON object with this exact format (no markdown, no extra text):
        {
          "riskLevel": "none|low|medium|high|critical",
          "keywordsDetected": [],
          "recommendedAction": "none|show_resources|show_crisis_hub|immediate_intervention",
          "reasoning": "..."
        }
        """;

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public CrisisDetector(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
    }

    public CrisisDetectionResultDto analyze(String text) {
        // Layer 1: Keyword-based detection (fast)
        List<String> detectedKeywords = detectKeywords(text);
        boolean keywordCrisis = !detectedKeywords.isEmpty();

        // Layer 2: AI-based semantic analysis
        CrisisDetectionResultDto aiResult = analyzeWithAi(text);

        // Combine signals - err on the side of caution
        return combineResults(detectedKeywords, keywordCrisis, aiResult);
    }

    public List<String> detectKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();
        List<String> detected = new ArrayList<>();
        for (String keyword : CRISIS_KEYWORDS) {
            if (lowerText.contains(keyword)) {
                detected.add(keyword);
            }
        }
        return detected;
    }

    private CrisisDetectionResultDto analyzeWithAi(String text) {
        try {
            String response = chatClient.prompt()
                .user(String.format(CRISIS_DETECTION_PROMPT, text))
                .call()
                .content();

            // Parse JSON response
            JsonNode json = objectMapper.readTree(response);
            CrisisDetectionResultDto result = new CrisisDetectionResultDto();
            result.setRiskLevel(json.get("riskLevel").asText());

            List<String> keywords = new ArrayList<>();
            if (json.has("keywordsDetected")) {
                for (JsonNode kw : json.get("keywordsDetected")) {
                    keywords.add(kw.asText());
                }
            }
            result.setKeywordsDetected(keywords);
            result.setRecommendedAction(json.get("recommendedAction").asText());
            result.setReasoning(json.get("reasoning").asText());
            return result;
        } catch (Exception e) {
            log.error("AI crisis analysis failed: {}", e.getMessage());
            // Return a safe default when AI fails
            return new CrisisDetectionResultDto("none", Collections.emptyList(), "none",
                "AI analysis unavailable");
        }
    }

    private CrisisDetectionResultDto combineResults(List<String> detectedKeywords,
                                                     boolean keywordCrisis,
                                                     CrisisDetectionResultDto aiResult) {
        // If keywords detected, ensure at least high risk
        if (keywordCrisis) {
            String riskLevel = aiResult.getRiskLevel();
            if ("none".equals(riskLevel) || "low".equals(riskLevel) || "medium".equals(riskLevel)) {
                riskLevel = "high";
            }

            // Merge keywords
            List<String> allKeywords = new ArrayList<>(detectedKeywords);
            if (aiResult.getKeywordsDetected() != null) {
                for (String kw : aiResult.getKeywordsDetected()) {
                    if (!allKeywords.contains(kw)) {
                        allKeywords.add(kw);
                    }
                }
            }

            String action = "critical".equals(riskLevel) ? "immediate_intervention" : "show_crisis_hub";

            return new CrisisDetectionResultDto(
                riskLevel,
                allKeywords,
                action,
                "Crisis keywords detected: " + String.join(", ", detectedKeywords) +
                    ". " + (aiResult.getReasoning() != null ? aiResult.getReasoning() : "")
            );
        }

        return aiResult;
    }
}
