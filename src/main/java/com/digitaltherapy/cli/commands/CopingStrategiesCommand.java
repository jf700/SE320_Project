package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.service.CrisisService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class CopingStrategiesCommand implements Command {
    private final CrisisService crisisService;
    public CopingStrategiesCommand(CrisisService c) { this.crisisService = c; }
    @Override public String getLabel() { return "Coping Strategies"; }
    @Override public void execute(Scanner scanner) {
        System.out.println("\n-- Coping Strategies --");
        crisisService.getCopingStrategies().forEach(s ->
            System.out.printf("  %s [%d min]%n    %s%n%n", s.title(), s.durationMinutes(), s.description()));
    }
}
