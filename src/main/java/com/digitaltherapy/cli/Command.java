package com.digitaltherapy.cli;

/**
 * Command Pattern interface for all CLI menu actions.
 */
public interface Command {
    /** Human-readable label shown in menus. */
    String getLabel();

    /** Execute the command with the given scanner for input. */
    void execute(java.util.Scanner scanner);
}
