package com.digitaltherapy.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
public class DigitalTherapyCLIClient {
    private static final Logger log = LoggerFactory.getLogger(DigitalTherapyCLIClient.class);

    private final MenuHandler menuHandler;
    private final CliSessionState sessionState;
    private final List<Command> authCommands;
    private final List<Command> sessionCommands;
    private final List<Command> diaryCommands;
    private final List<Command> progressCommands;
    private final List<Command> crisisCommands;

    @Autowired
    public DigitalTherapyCLIClient(MenuHandler menuHandler, CliSessionState sessionState,
                                   CliCommandGroups groups) {
        this.menuHandler = menuHandler;
        this.sessionState = sessionState;
        this.authCommands = groups.auth();
        this.sessionCommands = groups.sessions();
        this.diaryCommands = groups.diary();
        this.progressCommands = groups.progress();
        this.crisisCommands = groups.crisis();
    }

    public void start() {
        menuHandler.printBanner();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printStatusBar();
            running = menuHandler.showMenu("Main Menu", mainMenuCommands(), scanner);
        }
        System.out.println("\nThank you for using Digital Therapy Assistant. Take care!");
        scanner.close();
    }

    private List<Command> mainMenuCommands() {
        return List.of(
            menuItem("1. Authentication",     () -> runSubMenu("Authentication", authCommands)),
            menuItem("2. CBT Sessions",       () -> runSubMenu("CBT Sessions", sessionCommands)),
            menuItem("3. Thought Diary",      () -> runSubMenu("Thought Diary", diaryCommands)),
            menuItem("4. Progress Dashboard", () -> runSubMenu("Progress Dashboard", progressCommands)),
            menuItem("5. Crisis Support",     () -> runSubMenu("Crisis Support", crisisCommands)),
            menuItem("6. Settings",           () -> settingsSubMenu())
        );
    }

    private void runSubMenu(String title, List<Command> commands) {
        Scanner s = new Scanner(System.in);
        boolean open = true;
        while (open) open = menuHandler.showMenu(title, commands, s);
    }

    private void settingsSubMenu() {
        System.out.println("\n-- Settings --");
        System.out.println("  Logged in as: " + (sessionState.isLoggedIn() ? sessionState.getUserName() : "Guest"));
        System.out.println("  Swagger: http://localhost:8080/swagger-ui/index.html");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
    }

    private void printStatusBar() {
        if (sessionState.isLoggedIn()) System.out.printf("%n[Logged in as: %s]%n", sessionState.getUserName());
        else System.out.println("\n[Not logged in -- choose 1. Authentication -> Login]");
    }

    private Command menuItem(String label, Runnable action) {
        return new Command() {
            @Override public String getLabel() { return label; }
            @Override public void execute(Scanner scanner) { action.run(); }
        };
    }
}
