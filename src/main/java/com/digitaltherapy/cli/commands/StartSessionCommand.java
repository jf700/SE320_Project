package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.cli.MenuHandler;
import com.digitaltherapy.dto.request.SessionRequests.*;
import com.digitaltherapy.dto.response.SessionResponses.*;
import com.digitaltherapy.service.SessionService;
import org.springframework.stereotype.Component;
import java.util.Scanner;
import java.util.UUID;

@Component
public class StartSessionCommand implements Command {
    private final SessionService sessionService;
    private final CliSessionState state;
    public StartSessionCommand(SessionService s, CliSessionState st) { this.sessionService = s; this.state = st; }
    @Override public String getLabel() { return "Start New Session"; }
    @Override public void execute(Scanner scanner) {
        ViewSessionLibraryCommand.requireLogin(state);
        String idStr = MenuHandler.prompt("Enter Session ID (first 8 chars or full UUID)", scanner);
        if (idStr.isBlank()) { System.out.println("Required."); return; }
        UUID sessionId;
        try {
            sessionId = idStr.length() == 36 ? UUID.fromString(idStr) :
                sessionService.getSessionLibrary().stream().flatMap(m -> m.sessions().stream())
                    .filter(s -> s.id().toString().startsWith(idStr)).findFirst().map(SessionSummaryDto::id).orElse(null);
        } catch (Exception e) { System.out.println("Invalid ID."); return; }
        if (sessionId == null) { System.out.println("Session not found."); return; }
        int mood = MenuHandler.promptInt("Mood before (1-10)", 1, 10, scanner);
        ActiveSession active = sessionService.startSession(state.getUserId(), sessionId, mood > 0 ? new StartSessionRequest(mood) : null);
        state.setActiveUserSessionId(active.userSessionId());
        System.out.printf("Session started: %s%nType messages. Enter 'done' to end.%n", active.sessionTitle());
        while (true) {
            System.out.print("\nYou: ");
            String msg = scanner.nextLine().trim();
            if (msg.equalsIgnoreCase("done") || msg.equalsIgnoreCase("exit")) break;
            if (msg.isBlank()) continue;
            ChatResponse reply = sessionService.chat(active.userSessionId(), state.getUserId(), msg);
            System.out.println("\nAssistant: " + reply.message());
        }
        int moodAfter = MenuHandler.promptInt("Mood after (1-10)", 1, 10, scanner);
        sessionService.endSession(active.userSessionId(), new EndSessionRequest("completed", moodAfter > 0 ? moodAfter : null));
        System.out.println("Session complete!");
        state.setActiveUserSessionId(null);
    }
}
