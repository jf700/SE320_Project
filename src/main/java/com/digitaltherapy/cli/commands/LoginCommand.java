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
public class LoginCommand implements Command {
    private final AuthService authService;
    private final CliSessionState state;
    public LoginCommand(AuthService authService, CliSessionState state) { this.authService = authService; this.state = state; }
    @Override public String getLabel() { return "Login"; }
    @Override public void execute(Scanner scanner) {
        System.out.println("\n-- Login --");
        String email = MenuHandler.prompt("Email", scanner);
        String pass = MenuHandler.prompt("Password", scanner);
        if (email.isBlank() || pass.isBlank()) { System.out.println("Email and password required."); return; }
        AuthResponse resp = authService.login(new LoginRequest(email, pass));
        state.setUserId(resp.user().id()); state.setUserName(resp.user().name());
        state.setAccessToken(resp.accessToken()); state.setRefreshToken(resp.refreshToken());
        System.out.printf("Welcome back, %s!%n", resp.user().name());
    }
}
