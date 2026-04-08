package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.DiaryResponses.*;
import com.digitaltherapy.service.DiaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class ViewDiaryEntriesCommand implements Command {
    private final DiaryService diaryService;
    private final CliSessionState state;
    public ViewDiaryEntriesCommand(DiaryService d, CliSessionState st) { this.diaryService = d; this.state = st; }
    @Override public String getLabel() { return "View Entries"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        Page<DiaryEntrySummary> page = diaryService.getEntries(state.getUserId(), PageRequest.of(0, 10));
        System.out.println("\n-- Diary Entries --");
        if (page.isEmpty()) { System.out.println("No entries yet."); return; }
        for (DiaryEntrySummary e : page) {
            String sit = e.situation() != null && e.situation().length() > 40 ? e.situation().substring(0,39) + "..." : e.situation();
            System.out.printf("  [%s] %s  mood: %s->%s%n", e.createdAt().toLocalDate(), sit,
                e.moodBefore() != null ? e.moodBefore() : "?", e.moodAfter() != null ? e.moodAfter() : "?");
        }
    }
}
