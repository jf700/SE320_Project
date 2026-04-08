package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.ProgressResponses.*;
import com.digitaltherapy.service.ProgressService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class WeeklySummaryCommand implements Command {
    private final ProgressService progressService;
    private final CliSessionState state;
    public WeeklySummaryCommand(ProgressService p, CliSessionState st) { this.progressService = p; this.state = st; }
    @Override public String getLabel() { return "Weekly Summary"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        WeeklyProgress w = progressService.getWeeklyProgress(state.getUserId());
        System.out.printf("%n-- Weekly Progress --%n  Week: %s -> %s%n  Sessions: %d%n  Diary entries: %d%n  Avg mood change: %.1f%n",
            w.weekStart(), w.weekEnd(), w.sessionsCompleted(), w.diaryEntries(), w.averageMoodImprovement());
    }
}
