package com.digitaltherapy.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
public class MenuHandler {
    private static final Logger log = LoggerFactory.getLogger(MenuHandler.class);

    private static final String BANNER = """
            ╔══════════════════════════════════════════╗
            ║    Digital Therapy Assistant  v1.0       ║
            ║    CBT-Powered Burnout Recovery          ║
            ╚══════════════════════════════════════════╝
            """;

    public void printBanner() { System.out.println(BANNER); }

    public boolean showMenu(String title, List<Command> commands, Scanner scanner) {
        System.out.println("\n── " + title + " ──────────────────────────────");
        for (int i = 0; i < commands.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, commands.get(i).getLabel());
        }
        System.out.printf("  0. Back / Exit%n");
        System.out.print("Choose: ");
        String input = scanner.nextLine().trim();
        int choice;
        try { choice = Integer.parseInt(input); }
        catch (NumberFormatException e) { System.out.println("⚠  Please enter a number."); return true; }
        if (choice == 0) return false;
        if (choice < 1 || choice > commands.size()) { System.out.println("⚠  Invalid option."); return true; }
        try { commands.get(choice - 1).execute(scanner); }
        catch (Exception e) { log.error("Command error", e); System.out.println("✗  Error: " + e.getMessage()); }
        return true;
    }

    public static String prompt(String label, Scanner scanner) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    public static int promptInt(String label, int min, int max, Scanner scanner) {
        System.out.printf("%s (%d-%d): ", label, min, max);
        try {
            int v = Integer.parseInt(scanner.nextLine().trim());
            if (v < min || v > max) { System.out.printf("⚠  Must be between %d and %d.%n", min, max); return -1; }
            return v;
        } catch (NumberFormatException e) { System.out.println("⚠  Please enter a number."); return -1; }
    }
}
