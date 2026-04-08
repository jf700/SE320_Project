package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.ProgressResponses.*;
import com.digitaltherapy.service.ProgressService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class MonthlyTrendsCommand implements Command {
    private final ProgressService progressService;
    private final CliSessionState state;
    public MonthlyTrendsCommand(ProgressService p, CliSessionState st) { this.progressService = p; this.state = st; }
    @Override public String getLabel() { return "Monthly Trends"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        MonthlyTrends m = progressService.getMonthlyTrends(state.getUserId());
        System.out.printf("%n-- Monthly Trends (%d/%d) --%n  Sessions: %d%n  Diary entries: %d%n  Avg mood change: %.1f%n",
            m.month(), m.year(), m.totalSessions(), m.totalDiaryEntries(), m.averageMoodImprovement());
    }
}
