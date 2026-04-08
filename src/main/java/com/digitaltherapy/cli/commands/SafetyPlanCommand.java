package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.CrisisResponses.*;
import com.digitaltherapy.service.CrisisService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
public class SafetyPlanCommand implements Command {
    private final CrisisService crisisService;
    private final CliSessionState state;
    public SafetyPlanCommand(CrisisService c, CliSessionState st) { this.crisisService = c; this.state = st; }
    @Override public String getLabel() { return "Safety Plan"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        SafetyPlan plan = crisisService.getSafetyPlan(state.getUserId());
        System.out.println("\n-- My Safety Plan --");
        print("Warning signals", plan.warningSignals());
        print("Coping strategies", plan.copingStrategies());
        print("Social supports", plan.socialSupports());
        if (plan.reasonsForLiving() != null) System.out.println("  Reasons for living: " + plan.reasonsForLiving());
    }
    private void print(String title, List<String> items) {
        if (items == null || items.isEmpty()) return;
        System.out.println("  " + title + ":"); items.forEach(i -> System.out.println("    - " + i));
    }
}
