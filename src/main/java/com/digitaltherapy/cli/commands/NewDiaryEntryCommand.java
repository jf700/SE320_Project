package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.cli.MenuHandler;
import com.digitaltherapy.dto.request.DiaryRequests.*;
import com.digitaltherapy.dto.response.DiaryResponses.*;
import com.digitaltherapy.service.DiaryService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
public class NewDiaryEntryCommand implements Command {
    private final DiaryService diaryService;
    private final CliSessionState state;
    public NewDiaryEntryCommand(DiaryService d, CliSessionState st) { this.diaryService = d; this.state = st; }
    @Override public String getLabel() { return "New Entry"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        String situation = MenuHandler.prompt("Situation (what happened?)", scanner);
        if (situation.isBlank()) { System.out.println("Required."); return; }
        String thought = MenuHandler.prompt("Automatic thought", scanner);
        if (thought.isBlank()) { System.out.println("Required."); return; }
        int moodBefore = MenuHandler.promptInt("Mood right now (1-10)", 1, 10, scanner);
        List<DistortionSuggestion> suggestions = diaryService.suggestDistortions(thought);
        List<String> distortionIds = List.of();
        if (!suggestions.isEmpty()) {
            suggestions.forEach(s -> System.out.printf("  %s (%.0f%%)%n", s.distortionName(), s.confidence() * 100));
            if ("y".equalsIgnoreCase(MenuHandler.prompt("Accept? (y/n)", scanner)))
                distortionIds = suggestions.stream().map(DistortionSuggestion::distortionId).toList();
        }
        String alternative = MenuHandler.prompt("Alternative thought (optional)", scanner);
        int moodAfter = MenuHandler.promptInt("Mood after (0=skip)", 0, 10, scanner);
        DiaryEntryResponse resp = diaryService.createEntry(state.getUserId(),
            new DiaryEntryCreate(situation, thought, List.of(), distortionIds,
                alternative.isBlank() ? null : alternative,
                moodBefore > 0 ? moodBefore : null, moodAfter > 0 ? moodAfter : null, null, null));
        System.out.printf("Entry saved (id: %s)%n", resp.id().toString().substring(0, 8));
    }
}
