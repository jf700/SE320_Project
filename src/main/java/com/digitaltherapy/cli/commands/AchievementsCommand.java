package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.ProgressResponses.*;
import com.digitaltherapy.service.ProgressService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class AchievementsCommand implements Command {
    private final ProgressService progressService;
    private final CliSessionState state;
    public AchievementsCommand(ProgressService p, CliSessionState st) { this.progressService = p; this.state = st; }
    @Override public String getLabel() { return "Achievements"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        Achievements a = progressService.getAchievements(state.getUserId());
        System.out.println("\n-- Achievements --");
        if (!a.earned().isEmpty()) { System.out.println("  Earned:"); a.earned().forEach(ac -> System.out.printf("    [x] %s%n", ac.title())); }
        if (!a.inProgress().isEmpty()) { System.out.println("  In Progress:"); a.inProgress().forEach(ac -> System.out.printf("    [ ] %s (%d%%)%n", ac.title(), ac.progressPercent())); }
    }
}
