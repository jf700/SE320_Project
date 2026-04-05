package com.digitaltherapy.service;

import com.digitaltherapy.dto.*;

import java.util.List;
import java.util.UUID;

public interface AiService {

    // Generate therapeutic chat response
    ChatResponse generateResponse(UUID sessionId, String userMessage);

    // Suggest cognitive distortions from thought
    List<DistortionSuggestion> analyzeThought(String automaticThought);

    // Generate reframing prompts
    List<String> generateReframingPrompts(String thought, List<String> distortionIds);

    // Detect crisis indicators in text
    CrisisDetectionResultDto detectCrisis(String text);

    // Generate insights from diary entries
    DiaryInsights generateInsights(UUID userId);

    // Summarize session for completion
    SessionSummary summarizeSession(UUID sessionId);
}
