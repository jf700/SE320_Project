package com.digitaltherapy.cli.commands;
import com.digitaltherapy.cli.CliSessionState;
import com.digitaltherapy.cli.Command;
import com.digitaltherapy.cli.MenuHandler;
import com.digitaltherapy.dto.request.AuthRequests.*;
import com.digitaltherapy.dto.response.AuthResponses.*;
import com.digitaltherapy.service.AuthService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class RegisterCommand implements Command {
    private final AuthService authService;
    private final CliSessionState state;
    public RegisterCommand(AuthService authService, CliSessionState state) { this.authService = authService; this.state = state; }
    @Override public String getLabel() { return "Register"; }
    @Override public void execute(Scanner scanner) {
        System.out.println("\n-- Create Account --");
        String name = MenuHandler.prompt("Name", scanner);
        String email = MenuHandler.prompt("Email", scanner);
        String pass = MenuHandler.prompt("Password (min 8 chars)", scanner);
        if (name.isBlank() || email.isBlank() || pass.length() < 8) { System.out.println("Invalid input."); return; }
        AuthResponse resp = authService.register(new RegisterRequest(name, email, pass));
        state.setUserId(resp.user().id()); state.setUserName(resp.user().name());
        state.setAccessToken(resp.accessToken()); state.setRefreshToken(resp.refreshToken());
        System.out.printf("Welcome, %s!%n", resp.user().name());
    }
}
