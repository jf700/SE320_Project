package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.SessionResponses.*;
import com.digitaltherapy.service.SessionService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
public class ViewSessionLibraryCommand implements Command {
    private final SessionService sessionService;
    private final CliSessionState state;
    public ViewSessionLibraryCommand(SessionService s, CliSessionState st) { this.sessionService = s; this.state = st; }
    @Override public String getLabel() { return "View Session Library"; }
    @Override public void execute(Scanner scanner) {
        requireLogin(state);
        List<SessionModuleDto> modules = sessionService.getSessionLibrary();
        System.out.println("\n-- CBT Session Library --");
        if (modules.isEmpty()) { System.out.println("No sessions available."); return; }
        for (SessionModuleDto m : modules) {
            System.out.println("\n  Module: " + m.title());
            for (SessionSummaryDto s : m.sessions())
                System.out.printf("    [%s] %s (%d min)%n", s.id().toString().substring(0,8), s.title(), s.durationMinutes());
        }
    }
    public static void requireLogin(CliSessionState state) {
        if (!state.isLoggedIn()) throw new IllegalStateException("Please log in first.");
    }
}
