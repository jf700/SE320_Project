package com.digitaltherapy.service;

import com.digitaltherapy.dto.request.DiaryRequests.*;
import com.digitaltherapy.dto.response.DiaryResponses.*;
import com.digitaltherapy.entity.*;
import com.digitaltherapy.exception.ResourceNotFoundException;
import com.digitaltherapy.repository.*;
import com.digitaltherapy.service.impl.DiaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceImplTest {

    @Mock DiaryEntryRepository diaryEntryRepository;
    @Mock UserRepository userRepository;
    @Mock CognitiveDistortionRepository distortionRepository;

    @InjectMocks DiaryServiceImpl diaryService;

    private User user;
    private final UUID userId = UUID.randomUUID();
    private final UUID entryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName("Alice");
        user.setEmail("alice@example.com");
    }

    // ── createEntry ───────────────────────────────────────────────────────────

    @Test
    void createEntry_success() {
        DiaryEntry saved = new DiaryEntry();
        saved.setId(entryId);
        saved.setUser(user);
        saved.setSituation("Difficult meeting");
        saved.setAutomaticThought("I always mess up");
        saved.setMoodBefore(3);
        saved.setMoodAfter(5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(distortionRepository.findAllById(anyList())).thenReturn(List.of());
        when(diaryEntryRepository.save(any())).thenReturn(saved);

        DiaryEntryCreate req = new DiaryEntryCreate(
                "Difficult meeting", "I always mess up", List.of(),
                List.of(), null, 3, 5, null, null);

        DiaryEntryResponse result = diaryService.createEntry(userId, req);

        assertThat(result.id()).isEqualTo(entryId);
        assertThat(result.situation()).isEqualTo("Difficult meeting");
        assertThat(result.moodBefore()).isEqualTo(3);
        verify(diaryEntryRepository).save(any(DiaryEntry.class));
    }

    @Test
    void createEntry_throwsNotFound_whenUserMissing() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> diaryService.createEntry(userId,
                new DiaryEntryCreate("s", "t", null, null, null, null, null, null, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getEntries ────────────────────────────────────────────────────────────

    @Test
    void getEntries_returnsPageOfSummaries() {
        DiaryEntry entry = new DiaryEntry();
        entry.setId(entryId);
        entry.setUser(user);
        entry.setSituation("Test situation");
        entry.setAutomaticThought("Test thought");
        entry.setMoodBefore(4);
        entry.setMoodAfter(6);
        entry.setDeleted(false);

        Page<DiaryEntry> page = new PageImpl<>(List.of(entry));
        when(diaryEntryRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(
                eq(userId), any())).thenReturn(page);

        Page<DiaryEntrySummary> result = diaryService.getEntries(userId, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).situation()).isEqualTo("Test situation");
    }

    // ── getEntryDetail ────────────────────────────────────────────────────────

    @Test
    void getEntryDetail_returnsEntry() {
        DiaryEntry entry = new DiaryEntry();
        entry.setId(entryId);
        entry.setUser(user);
        entry.setSituation("Test");
        entry.setAutomaticThought("Thought");
        entry.setDeleted(false);

        when(diaryEntryRepository.findById(entryId)).thenReturn(Optional.of(entry));

        DiaryEntryResponse result = diaryService.getEntryDetail(entryId);
        assertThat(result.id()).isEqualTo(entryId);
    }

    @Test
    void getEntryDetail_throwsNotFound_whenDeleted() {
        DiaryEntry deleted = new DiaryEntry();
        deleted.setId(entryId);
        deleted.setDeleted(true);

        when(diaryEntryRepository.findById(entryId)).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> diaryService.getEntryDetail(entryId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── deleteEntry ───────────────────────────────────────────────────────────

    @Test
    void deleteEntry_setsSoftDeleteFlag() {
        DiaryEntry entry = new DiaryEntry();
        entry.setId(entryId);
        entry.setDeleted(false);

        when(diaryEntryRepository.findById(entryId)).thenReturn(Optional.of(entry));

        diaryService.deleteEntry(entryId);

        assertThat(entry.getDeleted()).isTrue();
        verify(diaryEntryRepository).save(entry);
    }

    @Test
    void deleteEntry_throwsNotFound_whenMissing() {
        when(diaryEntryRepository.findById(entryId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> diaryService.deleteEntry(entryId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── suggestDistortions ────────────────────────────────────────────────────

    @Test
    void suggestDistortions_findsAllOrNothingPattern() {
        CognitiveDistortion d = new CognitiveDistortion();
        d.setId("all-or-nothing");
        d.setName("All-or-Nothing Thinking");

        when(distortionRepository.findById(anyString())).thenReturn(Optional.empty());
        when(distortionRepository.findById("all-or-nothing")).thenReturn(Optional.of(d));

        List<DistortionSuggestion> suggestions =
                diaryService.suggestDistortions("I always fail and nobody ever helps me");

        assertThat(suggestions).anyMatch(s -> s.distortionId().equals("all-or-nothing"));
    }

    @Test
    void suggestDistortions_returnsEmpty_forNeutralThought() {
        List<DistortionSuggestion> suggestions =
                diaryService.suggestDistortions("I had lunch and took a walk today.");
        assertThat(suggestions).isEmpty();
    }

    // ── getInsights ───────────────────────────────────────────────────────────

    @Test
    void getInsights_returnsInsights() {
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(
                List.of(new DiaryEntry(), new DiaryEntry(), new DiaryEntry(), new DiaryEntry(), new DiaryEntry()));
        when(diaryEntryRepository.calculateAverageMoodImprovement(userId)).thenReturn(1.5);

        DiaryInsights insights = diaryService.getInsights(userId);

        assertThat(insights.totalEntries()).isEqualTo(5);
        assertThat(insights.averageMoodImprovement()).isEqualTo(1.5);
        assertThat(insights.aiInsight()).isNotBlank();
    }

    @Test
    void getInsights_handlesNullAverageMood() {
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of());
        when(diaryEntryRepository.calculateAverageMoodImprovement(userId)).thenReturn(null);

        DiaryInsights insights = diaryService.getInsights(userId);
        assertThat(insights.averageMoodImprovement()).isEqualTo(0.0);
    }
}
