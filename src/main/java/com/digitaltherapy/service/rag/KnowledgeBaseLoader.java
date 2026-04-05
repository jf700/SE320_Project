package com.digitaltherapy.service.rag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class KnowledgeBaseLoader {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseLoader.class);

    private final VectorStore vectorStore;
    private final String storePath;

    public KnowledgeBaseLoader(VectorStore vectorStore,
                                @Value("${spring.ai.vectorstore.simple.store.path:./data/vector-store.json}") String storePath) {
        this.vectorStore = vectorStore;
        this.storePath = storePath;
    }

    @PostConstruct
    public void loadKnowledgeBase() {
        File storeFile = new File(storePath);
        if (storeFile.exists() && storeFile.length() > 0) {
            log.info("Vector store already persisted at {}. Skipping knowledge base reload.",
                    storeFile.getAbsolutePath());
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        int totalLoaded = 0;

        // 1. Load Distortions
        totalLoaded += loadDistortions(mapper);

        // 2. Load CBT Techniques
        totalLoaded += loadCbtTechniques(mapper);

        // 3. Load Crisis Protocols
        totalLoaded += loadCrisisProtocols(mapper);

        log.info("Knowledge base loaded: {} total documents in vector store", totalLoaded);

        // Persist to disk
        if (vectorStore instanceof SimpleVectorStore svs) {
            storeFile.getParentFile().mkdirs();
            svs.save(storeFile);
            log.info("Vector store persisted to: {}", storeFile.getAbsolutePath());
        }
    }

    private int loadDistortions(ObjectMapper mapper) {
        List<Document> documents = new ArrayList<>();
        try {
            InputStream is = new ClassPathResource("knowledge-base/distortions.json").getInputStream();
            JsonNode root = mapper.readTree(is);

            for (JsonNode distortion : root) {
                String content = String.format(
                    "Cognitive Distortion: %s\nDescription: %s\nExamples: %s\nReframing Tips: %s",
                    distortion.get("name").asText(),
                    distortion.get("description").asText(),
                    distortion.get("examples").toString(),
                    distortion.get("reframingTips").toString()
                );
                Document doc = new Document(content,
                    Map.of("type", "distortion",
                           "id", distortion.get("id").asText(),
                           "name", distortion.get("name").asText()));
                documents.add(doc);
            }

            vectorStore.add(documents);
            log.info("Loaded {} cognitive distortion documents", documents.size());
        } catch (Exception e) {
            log.error("Failed to load distortions: {}", e.getMessage());
        }
        return documents.size();
    }

    private int loadCbtTechniques(ObjectMapper mapper) {
        List<Document> documents = new ArrayList<>();
        try {
            InputStream is = new ClassPathResource("knowledge-base/cbt-techniques.json").getInputStream();
            JsonNode root = mapper.readTree(is);

            for (JsonNode technique : root) {
                String content = String.format(
                    "CBT Technique: %s\nDescription: %s\nSteps: %s\nWhen to Use: %s",
                    technique.get("name").asText(),
                    technique.get("description").asText(),
                    technique.get("steps").toString(),
                    technique.get("whenToUse").asText()
                );
                Document doc = new Document(content,
                    Map.of("type", "technique",
                           "id", technique.get("id").asText(),
                           "name", technique.get("name").asText()));
                documents.add(doc);
            }

            vectorStore.add(documents);
            log.info("Loaded {} CBT technique documents", documents.size());
        } catch (Exception e) {
            log.error("Failed to load CBT techniques: {}", e.getMessage());
        }
        return documents.size();
    }

    private int loadCrisisProtocols(ObjectMapper mapper) {
        List<Document> documents = new ArrayList<>();
        try {
            InputStream is = new ClassPathResource("knowledge-base/crisis-protocols.json").getInputStream();
            JsonNode root = mapper.readTree(is);

            // Load warning signs
            JsonNode warningSigns = root.get("warningSignsRecognition");
            if (warningSigns != null) {
                Document doc = new Document(
                    "Crisis Warning Signs: " + warningSigns.toString(),
                    Map.of("type", "protocol", "category", "warning-signs"));
                documents.add(doc);
            }

            // Load de-escalation techniques
            JsonNode deEscalation = root.get("deEscalationTechniques");
            if (deEscalation != null) {
                Document doc = new Document(
                    "De-Escalation Techniques: " + deEscalation.toString(),
                    Map.of("type", "protocol", "category", "de-escalation"));
                documents.add(doc);
            }

            // Load safety planning steps
            JsonNode safetyPlanning = root.get("safetyPlanningSteps");
            if (safetyPlanning != null) {
                Document doc = new Document(
                    "Safety Planning Steps: " + safetyPlanning.toString(),
                    Map.of("type", "protocol", "category", "safety-planning"));
                documents.add(doc);
            }

            // Load emergency resources
            JsonNode emergencyResources = root.get("emergencyResources");
            if (emergencyResources != null) {
                Document doc = new Document(
                    "Emergency Resources: " + emergencyResources.toString(),
                    Map.of("type", "protocol", "category", "emergency-resources"));
                documents.add(doc);
            }

            vectorStore.add(documents);
            log.info("Loaded {} crisis protocol documents", documents.size());
        } catch (Exception e) {
            log.error("Failed to load crisis protocols: {}", e.getMessage());
        }
        return documents.size();
    }
}
