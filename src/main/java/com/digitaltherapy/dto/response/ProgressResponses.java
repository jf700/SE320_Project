package com.digitaltherapy.dto.response;

import java.time.LocalDate;
import java.util.List;

public final class ProgressResponses {

    private ProgressResponses() {}

    public record WeeklyProgress(
        LocalDate weekStart,
        LocalDate weekEnd,
        int sessionsCompleted,
        int diaryEntries,
        double averageMoodImprovement,
        int streakDays,
        List<DailyMood> dailyMoods
    ) {}

    public record DailyMood(LocalDate date, double averageMood) {}

    public record MonthlyTrends(
        int month,
        int year,
        int totalSessions,
        int totalDiaryEntries,
        double averageMoodImprovement,
        List<WeeklyProgress> weeklyBreakdown,
        List<String> topDistortions
    ) {}

    public record BurnoutRecovery(
        String currentPhase,
        int recoveryScorePercent,
        List<String> completedMilestones,
        List<String> nextSteps,
        String recommendation
    ) {}

    public record Achievements(
        List<Achievement> earned,
        List<Achievement> inProgress
    ) {}

    public record Achievement(
        String id,
        String title,
        String description,
        String icon,
        boolean earned,
        int progressPercent
    ) {}
}
