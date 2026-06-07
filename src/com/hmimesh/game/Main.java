package com.hmimesh.game;

import javax.swing.SwingUtilities;

/**
 * Entry point for Dungo — Java Swing Text RPG.
 *
 * <p>Compile:
 * <pre>
 *   mkdir -p out
 *   javac -d out src/com/hmimesh/game/*.java
 * </pre>
 *
 * Run:
 * <pre>
 *   java -cp out com.hmimesh.game.Main
 * </pre>
 *
 * @author Ben Farjun
 */
public class Main {

    /**
     * Launches the game on the Swing Event Dispatch Thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameEngine());
    }
}
