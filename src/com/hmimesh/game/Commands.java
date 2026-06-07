package com.hmimesh.game;

import java.util.ArrayList;
import java.util.List;

/**
 * In-game text commands the player can type at any time.
 *
 * <p>Each constant declares up to four alias strings (case-insensitive) and
 * an {@link #enable(GameWindow, Player)} body that executes the command.
 *
 * <p>New commands can be added by extending this enum with a new constant.
 *
 * @author Ben Farjun
 */
enum Commands {

    HELP("help", "HELP", "h", "Help") {
        @Override
        public void enable(GameWindow game, Player player) {
            game.print("Commands:");
            game.print("Before fight when facing doors:");
            game.print("  1 / left  / l — easy battle");
            game.print("  2 / right / r — hard battle");
            game.print("When asked yes/no:");
            game.print("  yes / y  —  agree");
            game.print("  no  / n  —  disagree");
            game.print("Any time:");
            game.print("  inventory / i — show inventory");
            game.print("  feats     / f — show current feats");
            game.print("  stats     / s — show current stats");
            game.print("  exit / quit   — quit the game");
            game.print("Terms:");
            game.print("  hp  = health points (reach 0 and you die)");
            game.print("  ac  = Armor Class  (harder to hit)");
            game.print("  xp  = experience   (fill bar to level up)");
            game.print("  mod = modifier     (flat damage bonus)");
        }
    },

    FEATS("feat", "f", "Feats", "F") {
        @Override
        public void enable(GameWindow game, Player player) {
            game.print("Feats: " + player.getFeats());
        }
    },

    INVENTORY("inventory", "i", "Inventory", "I") {
        @Override
        public void enable(GameWindow game, Player player) {
            game.print(player.getName() + "'s inventory:");
            game.print("|                        |");
            game.print("| ARMOR:  " + player.getArmor().getName() + "           |");
            game.print("| WEAPON: " + player.getWeapon()          + "           |");
            game.print("| ITEMS:  " + player.getBag()             + "           |");
            game.print("| Gold:   " + player.getGold()            + "           |");
        }
    },

    STATS("stats", "s", "Stats", "S") {
        @Override
        public void enable(GameWindow game, Player player) {
            game.print(player.getName() + "'s stats:");
            game.print("  AC:        " + player.getAc());
            game.print("  HP:        " + player.getHp() + "/" + player.getMaxHP());
            game.print("  Dice pool: " + player.getDiceCount());
        }
    },

    YES("y", "yes", "Y", "Yes") {
        @Override
        public void enable(GameWindow game, Player player) {
            // Only acts as a state-change shortcut in READY state;
            // other states use dedicated callbacks in GameEngine.
            if (game.getState() == GameState.READY) {
                game.setState(GameState.DOOR_CHOOSE);
            }
        }
    },

    NO("n", "no", "N", "NO") {
        @Override
        public void enable(GameWindow game, Player player) {
            if (game.getState() == GameState.READY) {
                game.setState(GameState.ENTER_NAME);
            }
        }
    },

    LEFT("left", "l", "Left", "1") {
        @Override
        public void enable(GameWindow game, Player player) {
            if (game.getState() == GameState.READY) {
                game.setState(GameState.EASY_FIGHT);
            }
        }
    },

    RIGHT("right", "r", "Right", "2") {
        @Override
        public void enable(GameWindow game, Player player) {
            if (game.getState() == GameState.READY) {
                game.setState(GameState.HARD_FIGHT);
            }
        }
    },

    EXIT("exit", "quit", "Quit", "Exit") {
        @Override
        public void enable(GameWindow game, Player player) {
            System.exit(0);
        }
    };

    // ─── Fields ─────────────────────────────────────────────────────────────

    private final String name1;
    private final String name2;
    private final String name3;
    private final String name4;

    Commands(String name1, String name2, String name3, String name4) {
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.name4 = name4;
    }

    // ─── Matching ────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code input} matches any of this command's aliases
     * (case-insensitive).
     *
     * @param input the raw text the player typed
     * @return {@code true} if it is a recognised alias for this command
     */
    public boolean matches(String input) {
        return input.equalsIgnoreCase(name1)
            || input.equalsIgnoreCase(name2)
            || input.equalsIgnoreCase(name3)
            || input.equalsIgnoreCase(name4);
    }

    /**
     * Scans all commands and returns the first one that matches {@code input}.
     *
     * @param input the raw text the player typed
     * @return the matching command, or {@code null} if none match
     */
    public static Commands fromInput(String input) {
        for (Commands cmd : Commands.values()) {
            if (cmd.matches(input)) return cmd;
        }
        return null;
    }

    // ─── Abstract ────────────────────────────────────────────────────────────

    /**
     * Executes this command's action.
     *
     * @param game   the active game window
     * @param player the current player
     */
    public abstract void enable(GameWindow game, Player player);
}
