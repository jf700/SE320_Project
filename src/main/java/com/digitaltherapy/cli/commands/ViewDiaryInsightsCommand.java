package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.DiaryResponses.*;
import com.digitaltherapy.service.DiaryService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class ViewDiaryInsightsCommand implements Command {
    private final DiaryService diaryService;
    private final CliSessionState state;
    public ViewDiaryInsightsCommand(DiaryService d, CliSessionState st) { this.diaryService = d; this.state = st; }
    @Override public String getLabel() { return "View Insights"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        DiaryInsights insights = diaryService.getInsights(state.getUserId());
        System.out.println("\n-- Diary Insights --");
        System.out.println("  Total entries:    " + insights.totalEntries());
        System.out.printf( "  Avg mood change:  %.1f points%n", insights.averageMoodImprovement());
        insights.patterns().forEach(p -> System.out.println("  -> " + p));
        System.out.println("\n  " + insights.aiInsight());
    }
}
