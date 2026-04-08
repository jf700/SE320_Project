package com.digitaltherapy.service;

import com.digitaltherapy.dto.*;
import com.digitaltherapy.entity.ChatMessage;
import com.digitaltherapy.entity.UserSession;
import com.digitaltherapy.entity.User;
import com.digitaltherapy.repository.ChatMessageRepository;
import com.digitaltherapy.repository.UserSessionRepository;
import com.digitaltherapy.service.impl.AiServiceImpl;
import com.digitaltherapy.service.rag.CrisisDetector;
import com.digitaltherapy.service.rag.RagContextBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceImplTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private RagContextBuilder ragContextBuilder;

    @Mock
    private CrisisDetector crisisDetector;

    @Mock
    private UserSessionRepository sessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    private AiServiceImpl aiService;

    @BeforeEach
    void setUp() {
        aiService = new AiServiceImpl(chatClient, ragContextBuilder, crisisDetector,
            sessionRepository, chatMessageRepository);
    }

    @Test
    void analyzeThought_returnsDistortionSuggestions() {
        // Arrange
        String thought = "I always fail at everything I do";
        String mockResponse = """
            [
              {
                "distortionId": "all-or-nothing",
                "confidence": 0.9,
                "reasoning": "The thought uses absolute terms like 'always' and 'everything'"
              },
              {
                "distortionId": "overgeneralization",
                "confidence": 0.85,
                "reasoning": "A single experience is being generalized to all situations"
              }
            ]
            """;

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(mockResponse);

        // Act
        List<DistortionSuggestion> suggestions = aiService.analyzeThought(thought);

        // Assert
        assertNotNull(suggestions);
        assertEquals(2, suggestions.size());
        assertEquals("all-or-nothing", suggestions.get(0).getDistortionId());
        assertEquals(0.9, suggestions.get(0).getConfidence(), 0.01);
        assertEquals("overgeneralization", suggestions.get(1).getDistortionId());
    }

    @Test
    void detectCrisis_withCrisisKeywords_returnsHighRisk() {
        // Arrange
        String text = "I want to kill myself, there's no point anymore";
        CrisisDetectionResultDto expectedResult = new CrisisDetectionResultDto(
            "high",
            List.of("kill myself"),
            "show_crisis_hub",
            "Crisis keywords detected: kill myself."
        );
        when(crisisDetector.analyze(text)).thenReturn(expectedResult);

        // Act
        CrisisDetectionResultDto result = aiService.detectCrisis(text);

        // Assert
        assertNotNull(result);
        assertEquals("high", result.getRiskLevel());
        assertTrue(result.getKeywordsDetected().contains("kill myself"));
        assertEquals("show_crisis_hub", result.getRecommendedAction());
    }

    @Test
    void detectCrisis_withSafeText_returnsNone() {
        // Arrange
        String text = "I had a really good day at work today";
        CrisisDetectionResultDto expectedResult = new CrisisDetectionResultDto(
            "none", Collections.emptyList(), "none", "No crisis indicators detected"
        );
        when(crisisDetector.analyze(text)).thenReturn(expectedResult);

        // Act
        CrisisDetectionResultDto result = aiService.detectCrisis(text);

        // Assert
        assertNotNull(result);
        assertEquals("none", result.getRiskLevel());
        assertTrue(result.getKeywordsDetected().isEmpty());
        assertEquals("none", result.getRecommendedAction());
    }

    @Test
    void generateResponse_buildsCorrectPromptWithRagContext() {
        // Arrange
        UUID sessionId = UUID.randomUUID();
        String userMessage = "I feel overwhelmed at work";
        String ragContext = "=== Relevant CBT Knowledge ===\nCognitive Distortion: Catastrophizing\n";

        UserSession session = new UserSession();
        session.setId(sessionId);
        User user = new User();
        user.setId(UUID.randomUUID());
        session.setUser(user);

        CrisisDetectionResultDto noCrisis = new CrisisDetectionResultDto(
            "none", Collections.emptyList(), "none", "No crisis indicators"
        );

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(ragContextBuilder.buildContext(any(UUID.class), eq(sessionId), eq(userMessage)))
            .thenReturn(ragContext);
        when(crisisDetector.analyze(userMessage)).thenReturn(noCrisis);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("I understand you're feeling overwhelmed. Let's explore that together.");

        // Act
        ChatResponse response = aiService.generateResponse(sessionId, userMessage);

        // Assert
        assertNotNull(response);
        assertEquals(sessionId, response.getSessionId());
        assertEquals("ASSISTANT", response.getRole());
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("overwhelmed"));
        assertNotNull(response.getCrisisAssessment());
        assertEquals("none", response.getCrisisAssessment().getRiskLevel());

        // Verify RAG context was built
        verify(ragContextBuilder).buildContext(any(UUID.class), eq(sessionId), eq(userMessage));
    }

    @Test
    void generateReframingPrompts_returnsPrompts() {
        // Arrange
        String thought = "I'm a complete failure";
        List<String> distortionIds = List.of("all-or-nothing", "labeling");
        String mockResponse = """
            [
              "What evidence supports the idea that you're a complete failure? What evidence contradicts it?",
              "Can you think of times when you succeeded at something?",
              "If a friend told you they were a complete failure, what would you say to them?"
            ]
            """;

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(mockResponse);

        // Act
        List<String> prompts = aiService.generateReframingPrompts(thought, distortionIds);

        // Assert
        assertNotNull(prompts);
        assertEquals(3, prompts.size());
        assertTrue(prompts.get(0).contains("evidence"));
    }

    @Test
    void summarizeSession_returnsSessionSummary() {
        // Arrange
        UUID sessionId = UUID.randomUUID();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage msg1 = new ChatMessage();
        msg1.setRole(ChatMessage.MessageRole.USER);
        msg1.setContent("I'm feeling stressed about work deadlines");
        messages.add(msg1);
        ChatMessage msg2 = new ChatMessage();
        msg2.setRole(ChatMessage.MessageRole.ASSISTANT);
        msg2.setContent("Let's explore that stress together.");
        messages.add(msg2);

        String mockResponse = """
            {
              "summary": "User discussed work-related stress and deadline anxiety.",
              "keyTopics": ["work stress", "deadlines"],
              "techniquesUsed": ["thought challenging"],
              "distortionsIdentified": ["catastrophizing"],
              "moodProgression": "Started anxious, ended more calm",
              "homeworkSuggestions": ["Practice daily thought records"]
            }
            """;

        when(chatMessageRepository.findByUserSessionIdOrderByTimestampAsc(sessionId))
            .thenReturn(messages);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(mockResponse);

        // Act
        SessionSummary summary = aiService.summarizeSession(sessionId);

        // Assert
        assertNotNull(summary);
        assertEquals(sessionId, summary.getSessionId());
        assertTrue(summary.getSummary().contains("work-related stress"));
        assertTrue(summary.getKeyTopics().contains("work stress"));
        assertTrue(summary.getDistortionsIdentified().contains("catastrophizing"));
    }
}
