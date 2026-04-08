package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.service.AuthService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class LogoutCommand implements Command {
    private final AuthService authService;
    private final CliSessionState state;
    public LogoutCommand(AuthService authService, CliSessionState state) { this.authService = authService; this.state = state; }
    @Override public String getLabel() { return "Logout"; }
    @Override public void execute(Scanner scanner) {
        if (!state.isLoggedIn()) { System.out.println("Not logged in."); return; }
        authService.logout(state.getAccessToken());
        String name = state.getUserName(); state.clear();
        System.out.printf("Goodbye, %s!%n", name);
    }
}
