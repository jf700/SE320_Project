package com.digitaltherapy.service;

import com.digitaltherapy.dto.*;
import com.digitaltherapy.entity.ChatMessage;
import com.digitaltherapy.entity.UserSession;
import com.digitaltherapy.repository.ChatMessageRepository;
import com.digitaltherapy.repository.UserSessionRepository;
import com.digitaltherapy.service.rag.CrisisDetector;
import com.digitaltherapy.service.rag.RagContextBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    private static final String THERAPEUTIC_SYSTEM_PROMPT = """
        You are a compassionate AI therapy assistant specializing in Cognitive
        Behavioral Therapy (CBT) for workplace burnout recovery. Your role is to:

        Maintain an empathetic, non-judgmental, and supportive tone
        Use Socratic questioning to help users explore their thoughts
        Guide users through CBT techniques like thought challenging
        Recognize and gently address cognitive distortions
        Celebrate progress and provide encouragement

        IMMEDIATELY recognize crisis indicators and respond appropriately

        Context about the user and relevant CBT knowledge:
        %s

        Remember:
        Never provide medical diagnoses
        Encourage professional help when appropriate
        Maintain appropriate boundaries
        Prioritize user safety above all else
        """;

    private static final String DISTORTION_ANALYSIS_PROMPT = """
        Analyze the following automatic thought for cognitive distortions.
        Return a JSON array of identified distortions with confidence scores.

        Thought: "%s"

        Available distortion types: %s

        Return ONLY a JSON array with this exact format (no markdown, no extra text):
        [
          {
            "distortionId": "distortion-id",
            "confidence": 0.85,
            "reasoning": "explanation of why this distortion applies"
          }
        ]
        """;

    private static final String REFRAMING_PROMPT = """
        Given the following thought and identified cognitive distortions, generate
        helpful reframing prompts that guide the user toward more balanced thinking.

        Original thought: "%s"
        Identified distortions: %s

        Return ONLY a JSON array of reframing prompt strings (no markdown, no extra text):
        ["prompt1", "prompt2", "prompt3"]
        """;

    private static final String INSIGHTS_PROMPT = """
        Based on the following diary entries and session data, generate insights
        about the user's patterns, progress, and recommendations.

        Data:
        %s

        Return ONLY a JSON object with this exact format (no markdown, no extra text):
        {
          "commonDistortions": ["distortion1", "distortion2"],
          "emotionFrequency": {"emotion": count},
          "averageMoodImprovement": 0.0,
          "patterns": ["pattern1", "pattern2"],
          "recommendations": ["recommendation1", "recommendation2"]
        }
        """;

    private static final String SESSION_SUMMARY_PROMPT = """
        Summarize the following therapy session transcript. Include key topics discussed,
        techniques used, distortions identified, mood progression, and homework suggestions.

        Transcript:
        %s

        Return ONLY a JSON object with this exact format (no markdown, no extra text):
        {
          "summary": "brief session summary",
          "keyTopics": ["topic1", "topic2"],
          "techniquesUsed": ["technique1"],
          "distortionsIdentified": ["distortion1"],
          "moodProgression": "description of mood changes",
          "homeworkSuggestions": ["suggestion1", "suggestion2"]
        }
        """;

    private static final List<String> DISTORTION_TYPES = List.of(
        "all-or-nothing", "overgeneralization", "mental-filtering",
        "catastrophizing", "mind-reading", "fortune-telling",
        "emotional-reasoning", "should-statements", "labeling", "personalization"
    );

    private final ChatClient chatClient;
    private final RagContextBuilder ragContextBuilder;
    private final CrisisDetector crisisDetector;
    private final UserSessionRepository sessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    public AiServiceImpl(ChatClient chatClient,
                          RagContextBuilder ragContextBuilder,
                          CrisisDetector crisisDetector,
                          UserSessionRepository sessionRepository,
                          ChatMessageRepository chatMessageRepository) {
        this.chatClient = chatClient;
        this.ragContextBuilder = ragContextBuilder;
        this.crisisDetector = crisisDetector;
        this.sessionRepository = sessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ChatResponse generateResponse(UUID sessionId, String userMessage) {
        log.debug("Generating response for session {} with message: {}", sessionId, userMessage);

        // First check for crisis
        CrisisDetectionResultDto crisisResult = detectCrisis(userMessage);

        // Get session and user context
        UUID userId = null;
        UserSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null && session.getUser() != null) {
            userId = session.getUser().getId();
        }

        // Build RAG context
        String context = ragContextBuilder.buildContext(userId, sessionId, userMessage);

        // Build prompt with system context
        String systemPrompt = String.format(THERAPEUTIC_SYSTEM_PROMPT, context);

        // Generate response
        String aiResponse = chatClient.prompt()
            .system(systemPrompt)
            .user(userMessage)
            .call()
            .content();

        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setMessage(aiResponse);
        response.setRole("ASSISTANT");
        response.setTimestamp(LocalDateTime.now());
        response.setCrisisAssessment(crisisResult);

        return response;
    }

    @Override
    public List<DistortionSuggestion> analyzeThought(String automaticThought) {
        log.debug("Analyzing thought for distortions: {}", automaticThought);

        String prompt = String.format(DISTORTION_ANALYSIS_PROMPT,
            automaticThought, String.join(", ", DISTORTION_TYPES));

        try {
            String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            return objectMapper.readValue(response,
                new TypeReference<List<DistortionSuggestion>>() {});
        } catch (Exception e) {
            log.error("Failed to analyze thought: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> generateReframingPrompts(String thought, List<String> distortionIds) {
        log.debug("Generating reframing prompts for thought: {}", thought);

        String prompt = String.format(REFRAMING_PROMPT, thought, String.join(", ", distortionIds));

        try {
            String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            return objectMapper.readValue(response, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("Failed to generate reframing prompts: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public CrisisDetectionResultDto detectCrisis(String text) {
        log.debug("Running crisis detection on text");
        return crisisDetector.analyze(text);
    }

    @Override
    public DiaryInsights generateInsights(UUID userId) {
        log.debug("Generating insights for user: {}", userId);

        String context = ragContextBuilder.buildContext(userId, null, "diary patterns and insights");
        String prompt = String.format(INSIGHTS_PROMPT, context);

        try {
            String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            return objectMapper.readValue(response, DiaryInsights.class);
        } catch (Exception e) {
            log.error("Failed to generate insights: {}", e.getMessage());
            DiaryInsights fallback = new DiaryInsights();
            fallback.setCommonDistortions(Collections.emptyList());
            fallback.setEmotionFrequency(Collections.emptyMap());
            fallback.setAverageMoodImprovement(0.0);
            fallback.setPatterns(Collections.emptyList());
            fallback.setRecommendations(List.of("Continue journaling to build more data for insights."));
            return fallback;
        }
    }

    @Override
    public SessionSummary summarizeSession(UUID sessionId) {
        log.debug("Summarizing session: {}", sessionId);

        List<ChatMessage> messages = chatMessageRepository.findByUserSessionIdOrderByTimestampAsc(sessionId);
        StringBuilder transcript = new StringBuilder();
        for (ChatMessage msg : messages) {
            transcript.append(String.format("%s: %s\n", msg.getRole(), msg.getContent()));
        }

        String prompt = String.format(SESSION_SUMMARY_PROMPT, transcript.toString());

        try {
            String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            SessionSummary summary = objectMapper.readValue(response, SessionSummary.class);
            summary.setSessionId(sessionId);
            return summary;
        } catch (Exception e) {
            log.error("Failed to summarize session: {}", e.getMessage());
            SessionSummary fallback = new SessionSummary();
            fallback.setSessionId(sessionId);
            fallback.setSummary("Session summary could not be generated.");
            fallback.setKeyTopics(Collections.emptyList());
            fallback.setTechniquesUsed(Collections.emptyList());
            fallback.setDistortionsIdentified(Collections.emptyList());
            fallback.setMoodProgression("Unable to determine");
            fallback.setHomeworkSuggestions(Collections.emptyList());
            return fallback;
        }
    }
}
