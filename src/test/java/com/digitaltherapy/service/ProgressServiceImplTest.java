package com.digitaltherapy.service;

import com.digitaltherapy.dto.response.ProgressResponses.*;
import com.digitaltherapy.entity.DiaryEntry;
import com.digitaltherapy.entity.UserSession;
import com.digitaltherapy.repository.*;
import com.digitaltherapy.service.impl.ProgressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceImplTest {

    @Mock UserSessionRepository userSessionRepository;
    @Mock DiaryEntryRepository diaryEntryRepository;

    @InjectMocks ProgressServiceImpl progressService;

    private final UUID userId = UUID.randomUUID();

    @Test
    void getWeeklyProgress_returnsProgress() {
        UserSession completed = new UserSession();
        completed.setStatus(UserSession.SessionStatus.COMPLETED);

        when(userSessionRepository.findByUserIdAndDateRange(eq(userId), any(), any()))
                .thenReturn(List.of(completed));
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId))
                .thenReturn(List.of(new DiaryEntry(), new DiaryEntry()));
        when(diaryEntryRepository.calculateAverageMoodImprovement(userId)).thenReturn(2.0);

        WeeklyProgress result = progressService.getWeeklyProgress(userId);

        assertThat(result.sessionsCompleted()).isEqualTo(1);
        assertThat(result.diaryEntries()).isEqualTo(2);
        assertThat(result.averageMoodImprovement()).isEqualTo(2.0);
        assertThat(result.dailyMoods()).hasSize(7);
    }

    @Test
    void getWeeklyProgress_handlesNullAvgMood() {
        when(userSessionRepository.findByUserIdAndDateRange(eq(userId), any(), any()))
                .thenReturn(List.of());
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of());
        when(diaryEntryRepository.calculateAverageMoodImprovement(userId)).thenReturn(null);

        WeeklyProgress result = progressService.getWeeklyProgress(userId);
        assertThat(result.averageMoodImprovement()).isEqualTo(0.0);
    }

    @Test
    void getMonthlyTrends_returnsTrends() {
        when(userSessionRepository.findByUserIdAndDateRange(eq(userId), any(), any()))
                .thenReturn(List.of(new UserSession(), new UserSession()));
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId))
                .thenReturn(List.of(new DiaryEntry()));
        when(diaryEntryRepository.calculateAverageMoodImprovement(userId)).thenReturn(1.5);

        MonthlyTrends result = progressService.getMonthlyTrends(userId);

        assertThat(result.totalSessions()).isEqualTo(2);
        assertThat(result.totalDiaryEntries()).isEqualTo(1);
    }

    @Test
    void getBurnoutRecovery_awarenessPhase() {
        when(userSessionRepository.countCompletedSessionsByUser(userId)).thenReturn(1L);
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of(new DiaryEntry()));

        BurnoutRecovery result = progressService.getBurnoutRecovery(userId);

        assertThat(result.currentPhase()).isEqualTo("Awareness");
        assertThat(result.recoveryScorePercent()).isEqualTo(15);
        assertThat(result.completedMilestones()).contains("Completed first CBT session", "Started thought journaling");
    }

    @Test
    void getBurnoutRecovery_actionPhase() {
        when(userSessionRepository.countCompletedSessionsByUser(userId)).thenReturn(6L);
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId))
                .thenReturn(List.of(new DiaryEntry(), new DiaryEntry()));

        BurnoutRecovery result = progressService.getBurnoutRecovery(userId);

        assertThat(result.currentPhase()).isEqualTo("Action");
        assertThat(result.recoveryScorePercent()).isEqualTo(70);
    }

    @Test
    void getBurnoutRecovery_recoveryPhase() {
        when(userSessionRepository.countCompletedSessionsByUser(userId)).thenReturn(10L);
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of());

        BurnoutRecovery result = progressService.getBurnoutRecovery(userId);

        assertThat(result.currentPhase()).isEqualTo("Recovery");
        assertThat(result.recoveryScorePercent()).isEqualTo(100);
    }

    @Test
    void getAchievements_noProgress() {
        when(userSessionRepository.countCompletedSessionsByUser(userId)).thenReturn(0L);
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of());

        Achievements result = progressService.getAchievements(userId);

        assertThat(result.earned()).isEmpty();
        assertThat(result.inProgress()).hasSize(3);
    }

    @Test
    void getAchievements_firstSessionEarned() {
        when(userSessionRepository.countCompletedSessionsByUser(userId)).thenReturn(1L);
        when(diaryEntryRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of(new DiaryEntry()));

        Achievements result = progressService.getAchievements(userId);

        assertThat(result.earned()).hasSize(2);
        assertThat(result.earned()).anyMatch(a -> a.id().equals("first-session"));
        assertThat(result.earned()).anyMatch(a -> a.id().equals("first-entry"));
        assertThat(result.inProgress()).hasSize(1);
    }
}
