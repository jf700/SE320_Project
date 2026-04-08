package com.digitaltherapy.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class MenuHandlerTest {

    private MenuHandler handler;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        handler = new MenuHandler();
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void printBanner_writesToStdOut() {
        handler.printBanner();
        assertThat(out.toString()).contains("Digital Therapy Assistant");
    }

    @Test
    void showMenu_returnsTrue_whenValidCommandChosen() {
        AtomicBoolean executed = new AtomicBoolean(false);
        Command cmd = new Command() {
            @Override public String getLabel() { return "Test"; }
            @Override public void execute(Scanner s) { executed.set(true); }
        };

        Scanner scanner = scannerOf("1");
        boolean result = handler.showMenu("Test Menu", List.of(cmd), scanner);

        assertThat(result).isTrue();
        assertThat(executed.get()).isTrue();
    }

    @Test
    void showMenu_returnsFalse_whenZeroEntered() {
        Scanner scanner = scannerOf("0");
        boolean result = handler.showMenu("Test", List.of(dummyCmd()), scanner);
        assertThat(result).isFalse();
    }

    @Test
    void showMenu_returnsTrue_whenInvalidNumberEntered() {
        Scanner scanner = scannerOf("99");
        boolean result = handler.showMenu("Test", List.of(dummyCmd()), scanner);
        assertThat(result).isTrue();
        assertThat(out.toString()).contains("Invalid option");
    }

    @Test
    void showMenu_returnsTrue_whenNonNumericEntered() {
        Scanner scanner = scannerOf("abc");
        boolean result = handler.showMenu("Test", List.of(dummyCmd()), scanner);
        assertThat(result).isTrue();
        assertThat(out.toString()).contains("Please enter a number");
    }

    @Test
    void showMenu_handlesCommandException_gracefully() {
        Command boom = new Command() {
            @Override public String getLabel() { return "Boom"; }
            @Override public void execute(Scanner s) { throw new RuntimeException("Test error"); }
        };
        Scanner scanner = scannerOf("1");
        boolean result = handler.showMenu("Test", List.of(boom), scanner);
        assertThat(result).isTrue();
        assertThat(out.toString()).contains("Error");
    }

    @Test
    void prompt_returnsUserInput() {
        Scanner scanner = scannerOf("hello world");
        String result = MenuHandler.prompt("Enter something", scanner);
        assertThat(result).isEqualTo("hello world");
    }

    @Test
    void promptInt_returnsValidInt() {
        Scanner scanner = scannerOf("7");
        int result = MenuHandler.promptInt("Mood", 1, 10, scanner);
        assertThat(result).isEqualTo(7);
    }

    @Test
    void promptInt_returnsMinusOne_onOutOfRange() {
        Scanner scanner = scannerOf("99");
        int result = MenuHandler.promptInt("Mood", 1, 10, scanner);
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void promptInt_returnsMinusOne_onNonNumeric() {
        Scanner scanner = scannerOf("abc");
        int result = MenuHandler.promptInt("Mood", 1, 10, scanner);
        assertThat(result).isEqualTo(-1);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Scanner scannerOf(String input) {
        return new Scanner(new ByteArrayInputStream((input + "\n").getBytes()));
    }

    private Command dummyCmd() {
        return new Command() {
            @Override public String getLabel() { return "Dummy"; }
            @Override public void execute(Scanner s) {}
        };
    }
}
