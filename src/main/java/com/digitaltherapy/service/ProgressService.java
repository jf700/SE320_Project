package com.digitaltherapy.service;

import com.digitaltherapy.dto.response.ProgressResponses.*;

import java.util.UUID;

public interface ProgressService {
    WeeklyProgress getWeeklyProgress(UUID userId);
    MonthlyTrends getMonthlyTrends(UUID userId);
    BurnoutRecovery getBurnoutRecovery(UUID userId);
    Achievements getAchievements(UUID userId);
}
