package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.SessionResponses.*;
import com.digitaltherapy.service.SessionService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
public class ViewSessionHistoryCommand implements Command {
    private final SessionService sessionService;
    private final CliSessionState state;
    public ViewSessionHistoryCommand(SessionService s, CliSessionState st) { this.sessionService = s; this.state = st; }
    @Override public String getLabel() { return "View Session History"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        List<SessionHistoryEntry> history = sessionService.getSessionHistory(state.getUserId());
        System.out.println("\n-- Session History --");
        if (history.isEmpty()) { System.out.println("No sessions yet."); return; }
        for (SessionHistoryEntry h : history)
            System.out.printf("  %-30s  %s  mood: %s->%s%n", h.sessionTitle(), h.status(),
                h.moodBefore() != null ? h.moodBefore() : "?", h.moodAfter() != null ? h.moodAfter() : "?");
    }
}
