package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.dto.response.CrisisResponses.*;
import com.digitaltherapy.service.CrisisService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class EmergencyResourcesCommand implements Command {
    private final CrisisService crisisService;
    public EmergencyResourcesCommand(CrisisService c) { this.crisisService = c; }
    @Override public String getLabel() { return "Emergency Resources"; }
    @Override public void execute(Scanner scanner) {
        CrisisHub hub = crisisService.getCrisisHub();
        System.out.println("\n-- Emergency Resources --\n  " + hub.message());
        hub.emergencyResources().forEach(r ->
            System.out.printf("%n  %s: %s%n  %s%n", r.name(), r.phone(), r.description()));
    }
}
