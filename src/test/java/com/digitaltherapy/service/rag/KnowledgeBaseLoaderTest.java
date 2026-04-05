package com.digitaltherapy.service.rag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeBaseLoaderTest {

    @Mock
    private SimpleVectorStore vectorStore;

    @TempDir
    Path tempDir;

    private KnowledgeBaseLoader knowledgeBaseLoader;

    @BeforeEach
    void setUp() {
        String storePath = tempDir.resolve("vector-store.json").toString();
        knowledgeBaseLoader = new KnowledgeBaseLoader(vectorStore, storePath);
    }

    @Test
    void loadKnowledgeBase_loadsAllJsonFiles() {
        // Act
        knowledgeBaseLoader.loadKnowledgeBase();

        // Assert - verify documents were added to vector store
        // Should be called 3 times: distortions, techniques, protocols
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore, times(3)).add(captor.capture());

        List<List<Document>> allCalls = captor.getAllValues();

        // Distortions: 10 documents
        assertEquals(10, allCalls.get(0).size(), "Should load 10 cognitive distortions");

        // Techniques: 5 documents
        assertEquals(5, allCalls.get(1).size(), "Should load 5 CBT techniques");

        // Protocols: 4 documents (warning signs, de-escalation, safety planning, emergency resources)
        assertEquals(4, allCalls.get(2).size(), "Should load 4 crisis protocol documents");
    }

    @Test
    void loadKnowledgeBase_distortionDocumentsHaveCorrectMetadata() {
        // Act
        knowledgeBaseLoader.loadKnowledgeBase();

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore, times(3)).add(captor.capture());

        List<Document> distortionDocs = captor.getAllValues().get(0);
        Document firstDoc = distortionDocs.get(0);

        assertEquals("distortion", firstDoc.getMetadata().get("type"));
        assertNotNull(firstDoc.getMetadata().get("id"));
        assertNotNull(firstDoc.getMetadata().get("name"));
        assertTrue(firstDoc.getText().contains("Cognitive Distortion:"));
    }

    @Test
    void loadKnowledgeBase_techniqueDocumentsHaveCorrectMetadata() {
        // Act
        knowledgeBaseLoader.loadKnowledgeBase();

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore, times(3)).add(captor.capture());

        List<Document> techniqueDocs = captor.getAllValues().get(1);
        Document firstDoc = techniqueDocs.get(0);

        assertEquals("technique", firstDoc.getMetadata().get("type"));
        assertTrue(firstDoc.getText().contains("CBT Technique:"));
    }

    @Test
    void loadKnowledgeBase_skipsWhenStoreExists() {
        // Arrange - create existing store file
        File existingStore = tempDir.resolve("existing-store.json").toFile();
        try {
            existingStore.createNewFile();
            java.nio.file.Files.writeString(existingStore.toPath(), "{\"data\": \"existing\"}");
        } catch (Exception e) {
            fail("Could not create test file");
        }

        KnowledgeBaseLoader loader = new KnowledgeBaseLoader(vectorStore, existingStore.getAbsolutePath());

        // Act
        loader.loadKnowledgeBase();

        // Assert - verify no documents were added
        verify(vectorStore, never()).add(anyList());
    }

    @Test
    void loadKnowledgeBase_persistsStoreToDisk() {
        // Act
        knowledgeBaseLoader.loadKnowledgeBase();

        // Assert - verify save was called
        verify(vectorStore).save(any(File.class));
    }
}
