package com.digitaltherapy.service.impl;

import com.digitaltherapy.dto.response.ProgressResponses.*;
import com.digitaltherapy.entity.UserSession;
import com.digitaltherapy.repository.*;
import com.digitaltherapy.service.ProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@Service
public class ProgressServiceImpl implements ProgressService {
    private final UserSessionRepository userSessionRepository;
    private final DiaryEntryRepository diaryEntryRepository;

    public ProgressServiceImpl(UserSessionRepository userSessionRepository, DiaryEntryRepository diaryEntryRepository) {
        this.userSessionRepository = userSessionRepository; this.diaryEntryRepository = diaryEntryRepository;
    }

    @Override @Transactional(readOnly = true)
    public WeeklyProgress getWeeklyProgress(UUID userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        List<UserSession> sessions = userSessionRepository.findByUserIdAndDateRange(userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX));
        long completed = sessions.stream().filter(s -> s.getStatus() == UserSession.SessionStatus.COMPLETED).count();
        long diaryCount = diaryEntryRepository.findByUserIdAndDeletedFalse(userId).size();
        Double avgMood = diaryEntryRepository.calculateAverageMoodImprovement(userId);
        List<DailyMood> dailyMoods = new ArrayList<>();
        for (int i = 0; i < 7; i++) dailyMoods.add(new DailyMood(weekStart.plusDays(i), 0.0));
        return new WeeklyProgress(weekStart, weekEnd, (int) completed, (int) Math.min(diaryCount, 7), avgMood != null ? avgMood : 0.0, 0, dailyMoods);
    }

    @Override @Transactional(readOnly = true)
    public MonthlyTrends getMonthlyTrends(UUID userId) {
        LocalDate now = LocalDate.now();
        List<UserSession> sessions = userSessionRepository.findByUserIdAndDateRange(userId,
                now.withDayOfMonth(1).atStartOfDay(), now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX));
        long diaryCount = diaryEntryRepository.findByUserIdAndDeletedFalse(userId).size();
        Double avgMood = diaryEntryRepository.calculateAverageMoodImprovement(userId);
        return new MonthlyTrends(now.getMonthValue(), now.getYear(), sessions.size(), (int) diaryCount, avgMood != null ? avgMood : 0.0, List.of(), List.of());
    }

    @Override @Transactional(readOnly = true)
    public BurnoutRecovery getBurnoutRecovery(UUID userId) {
        long completed = userSessionRepository.countCompletedSessionsByUser(userId);
        long diaryCount = diaryEntryRepository.findByUserIdAndDeletedFalse(userId).size();
        int score = (int) Math.min(100, (completed * 10) + (diaryCount * 5));
        String phase = score < 25 ? "Awareness" : score < 50 ? "Acceptance" : score < 75 ? "Action" : "Recovery";
        List<String> milestones = new ArrayList<>();
        if (completed >= 1) milestones.add("Completed first CBT session");
        if (diaryCount >= 1) milestones.add("Started thought journaling");
        List<String> nextSteps = new ArrayList<>();
        if (completed < 5) nextSteps.add("Complete " + (5 - completed) + " more CBT sessions");
        return new BurnoutRecovery(phase, score, milestones, nextSteps, "You're making real progress!");
    }

    @Override @Transactional(readOnly = true)
    public Achievements getAchievements(UUID userId) {
        long sessions = userSessionRepository.countCompletedSessionsByUser(userId);
        long entries = diaryEntryRepository.findByUserIdAndDeletedFalse(userId).size();
        List<Achievement> earned = new ArrayList<>(), inProgress = new ArrayList<>();
        add("first-session","First Step","Complete your first CBT session","🌱", sessions>=1, sessions>=1?100:0, earned, inProgress);
        add("five-sessions","Committed","Complete 5 CBT sessions","⭐", sessions>=5, (int)Math.min(100,sessions*20), earned, inProgress);
        add("first-entry","Self-Aware","Write your first diary entry","📓", entries>=1, entries>=1?100:0, earned, inProgress);
        return new Achievements(earned, inProgress);
    }

    private void add(String id, String title, String desc, String icon, boolean earned, int pct, List<Achievement> e, List<Achievement> ip) {
        Achievement a = new Achievement(id, title, desc, icon, earned, pct);
        if (earned) e.add(a); else ip.add(a);
    }
}
