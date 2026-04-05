package com.digitaltherapy.service.rag;

import com.digitaltherapy.entity.ChatMessage;
import com.digitaltherapy.entity.DiaryEntry;
import com.digitaltherapy.entity.UserSession;
import com.digitaltherapy.repository.ChatMessageRepository;
import com.digitaltherapy.repository.DiaryEntryRepository;
import com.digitaltherapy.repository.UserSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RagContextBuilder {

    private static final Logger log = LoggerFactory.getLogger(RagContextBuilder.class);

    private final VectorStore vectorStore;
    private final UserSessionRepository sessionRepository;
    private final DiaryEntryRepository diaryRepository;
    private final ChatMessageRepository chatMessageRepository;

    public RagContextBuilder(VectorStore vectorStore,
                              UserSessionRepository sessionRepository,
                              DiaryEntryRepository diaryRepository,
                              ChatMessageRepository chatMessageRepository) {
        this.vectorStore = vectorStore;
        this.sessionRepository = sessionRepository;
        this.diaryRepository = diaryRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public String buildContext(UUID userId, UUID sessionId, String query) {
        StringBuilder context = new StringBuilder();

        // 1. Retrieve relevant CBT knowledge via vector similarity search
        try {
            List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(5).build()
            );
            if (!relevantDocs.isEmpty()) {
                context.append("=== Relevant CBT Knowledge ===\n");
                for (Document doc : relevantDocs) {
                    context.append(doc.getText()).append("\n\n");
                }
            }
        } catch (Exception e) {
            log.warn("Vector similarity search failed: {}", e.getMessage());
        }

        // 2. Retrieve relevant past session data via similarity search
        // (Already covered by vector search above for CBT knowledge)

        // 3. Get user's recent session history
        if (userId != null) {
            try {
                List<UserSession> recentSessions = sessionRepository.findByUserIdOrderByStartedAtDesc(userId);
                if (!recentSessions.isEmpty()) {
                    context.append("=== User Session History ===\n");
                    int count = 0;
                    for (UserSession session : recentSessions) {
                        if (count >= 5) break;
                        context.append(String.format("Session %s - Status: %s, Started: %s\n",
                            session.getId(), session.getStatus(), session.getStartedAt()));
                        if (session.getMoodBefore() != null) {
                            context.append(String.format("  Mood: %d -> %d\n",
                                session.getMoodBefore(),
                                session.getMoodAfter() != null ? session.getMoodAfter() : 0));
                        }
                        count++;
                    }
                    context.append("\n");
                }
            } catch (Exception e) {
                log.warn("Failed to retrieve session history: {}", e.getMessage());
            }
        }

        // 4. Get user's diary patterns
        if (userId != null) {
            try {
                List<DiaryEntry> diaryEntries = diaryRepository.findByUserIdAndDeletedFalse(userId);
                if (!diaryEntries.isEmpty()) {
                    context.append("=== User Diary Patterns ===\n");
                    int count = 0;
                    for (DiaryEntry entry : diaryEntries) {
                        if (count >= 5) break;
                        context.append(String.format("Entry: Situation: %s, Thought: %s\n",
                            entry.getSituation(),
                            entry.getAutomaticThought()));
                        if (entry.getAlternativeThought() != null) {
                            context.append(String.format("  Alternative: %s\n", entry.getAlternativeThought()));
                        }
                        count++;
                    }
                    context.append("\n");
                }
            } catch (Exception e) {
                log.warn("Failed to retrieve diary entries: {}", e.getMessage());
            }
        }

        // 5. Get current session transcript
        if (sessionId != null) {
            try {
                List<ChatMessage> messages = chatMessageRepository
                    .findByUserSessionIdOrderByTimestampAsc(sessionId);
                if (!messages.isEmpty()) {
                    context.append("=== Current Session Transcript ===\n");
                    for (ChatMessage msg : messages) {
                        context.append(String.format("%s: %s\n",
                            msg.getRole(), msg.getContent()));
                    }
                    context.append("\n");
                }
            } catch (Exception e) {
                log.warn("Failed to retrieve session transcript: {}", e.getMessage());
            }
        }

        return context.toString();
    }
}
