package com.digitaltherapy.service.rag;

import com.digitaltherapy.dto.CrisisDetectionResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrisisDetectorTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private CrisisDetector crisisDetector;

    @BeforeEach
    void setUp() {
        crisisDetector = new CrisisDetector(chatClient);
    }

    @Test
    void detectKeywords_withCrisisText_returnsKeywords() {
        String text = "I want to kill myself and end it all";

        List<String> keywords = crisisDetector.detectKeywords(text);

        assertFalse(keywords.isEmpty());
        assertTrue(keywords.contains("kill myself"));
        assertTrue(keywords.contains("end it all"));
    }

    @Test
    void detectKeywords_withSafeText_returnsEmpty() {
        String text = "I had a productive day at work and feel good about my progress";

        List<String> keywords = crisisDetector.detectKeywords(text);

        assertTrue(keywords.isEmpty());
    }

    @Test
    void detectKeywords_withNullText_returnsEmpty() {
        List<String> keywords = crisisDetector.detectKeywords(null);
        assertTrue(keywords.isEmpty());
    }

    @Test
    void detectKeywords_withEmptyText_returnsEmpty() {
        List<String> keywords = crisisDetector.detectKeywords("");
        assertTrue(keywords.isEmpty());
    }

    @Test
    void detectKeywords_caseInsensitive() {
        String text = "I want to KILL MYSELF";

        List<String> keywords = crisisDetector.detectKeywords(text);

        assertFalse(keywords.isEmpty());
        assertTrue(keywords.contains("kill myself"));
    }

    @Test
    void analyze_withCrisisKeywords_triggersHighRisk() {
        String text = "I can't go on anymore, I want to hurt myself";
        String aiResponse = """
            {
              "riskLevel": "high",
              "keywordsDetected": ["hurt myself"],
              "recommendedAction": "show_crisis_hub",
              "reasoning": "User expressing desire to self-harm"
            }
            """;

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(aiResponse);

        CrisisDetectionResultDto result = crisisDetector.analyze(text);

        assertNotNull(result);
        assertEquals("high", result.getRiskLevel());
        assertTrue(result.getKeywordsDetected().contains("can't go on"));
        assertTrue(result.getKeywordsDetected().contains("hurt myself"));
        assertEquals("show_crisis_hub", result.getRecommendedAction());
    }

    @Test
    void analyze_withSafeText_returnsNone() {
        String text = "I'm learning new coping strategies and feeling hopeful";
        String aiResponse = """
            {
              "riskLevel": "none",
              "keywordsDetected": [],
              "recommendedAction": "none",
              "reasoning": "User is expressing positive coping and hope"
            }
            """;

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(aiResponse);

        CrisisDetectionResultDto result = crisisDetector.analyze(text);

        assertNotNull(result);
        assertEquals("none", result.getRiskLevel());
        assertTrue(result.getKeywordsDetected().isEmpty());
        assertEquals("none", result.getRecommendedAction());
    }

    @Test
    void analyze_keywordDetection_overridesLowAiRisk() {
        // AI says low risk but keywords detected - should escalate to high
        String text = "Sometimes I think about suicide but I'm okay";
        String aiResponse = """
            {
              "riskLevel": "low",
              "keywordsDetected": [],
              "recommendedAction": "show_resources",
              "reasoning": "Mentions suicide but states being okay"
            }
            """;

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(aiResponse);

        CrisisDetectionResultDto result = crisisDetector.analyze(text);

        assertNotNull(result);
        assertEquals("high", result.getRiskLevel());
        assertTrue(result.getKeywordsDetected().contains("suicide"));
    }

    @Test
    void analyze_aiFailure_returnsSafeDefault() {
        String text = "Some text that causes an error";

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new RuntimeException("AI service unavailable"));

        CrisisDetectionResultDto result = crisisDetector.analyze(text);

        assertNotNull(result);
        // No keywords detected in this text, so AI default is returned
        assertEquals("none", result.getRiskLevel());
    }
}
