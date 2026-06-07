package com.hmimesh.game;

/**
 * ANSI terminal colour codes used for coloured console output.
 *
 * <p>Works in WSL, PowerShell (Windows Terminal), Linux, and macOS terminals.
 * May not display correctly in basic Windows {@code cmd.exe} or BlueJ.
 * Call {@link #get()} rather than using the enum name directly so that
 * colour output is automatically suppressed in non-terminal environments.
 *
 * @author Ben Farjun
 */
enum Acolor {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    ORANGE("\u001B[38;5;208m"),
    BGREEN("\u001B[38;5;46m"),
    BRED("\u001B[38;5;196m"),
    PINK("\u001B[38;5;213m"),
    BBLUE("\u001B[38;5;39m"),
    BORANGE("\u001B[38;5;214m"),
    RESET("\u001B[0m");

    private final String code;

    Acolor(String code) {
        this.code = code;
    }

    /**
     * Returns the ANSI escape code if a terminal is detected, otherwise
     * returns an empty string so the text stays plain.
     *
     * @return ANSI colour escape sequence, or {@code ""}
     */
    public String get() {
        return AcolorConfig.USE_COLOR ? code : "";
    }
}
