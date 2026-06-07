package com.hmimesh.game;

/**
 * Secret cheat codes that modify the player's stats when entered as a name.
 *
 * <p>Type {@code GOD} as your character name to activate god mode.
 * Type {@code SEND HELP} for the opposite experience.
 *
 * @author Ben Farjun
 */
enum Cheats {

    GOD("God mode", 100, 1000, 10, 3, 5, 5) {
        @Override
        public void active(Player p) {
            p.setMaxHP(100);
            p.setGold(1000);
            p.setModifier(10);
            p.setDiceCount(3);
            p.setAc(p.getAc() + 5);
            p.setLuck(5);
        }
    },

    SENDHELP("Send Help", 5, 0, -2, 0, -3, -3) {
        @Override
        public void active(Player p) {
            p.setName(name);
            p.setMaxHP(hp);
            p.setGold(gold);
            p.setModifier(modifier);
            p.setDiceCount(diceCount);
            p.setAc(p.getAc() + ac);
            p.setLuck(-4);
        }
    };

    // ─── Fields ─────────────────────────────────────────────────────────────

    String name;
    int hp;
    int gold;
    int modifier;
    int diceCount;
    int ac;

    // ─── Constructor ────────────────────────────────────────────────────────

    Cheats(String name, int hp, int gold, int modifier, int diceCount, int ac, int luck) {
        this.name      = name;
        this.hp        = hp;
        this.gold      = gold;
        this.modifier  = modifier;
        this.diceCount = diceCount;
        this.ac        = ac;
        // luck parameter kept for constructor signature compatibility
    }

    // ─── Abstract ───────────────────────────────────────────────────────────

    /**
     * Applies this cheat's stat modifications to the given player.
     *
     * @param p the player to modify
     */
    public abstract void active(Player p);

    /**
     * Prints a notification to the console that a cheat is active.
     */
    public void display() {
        System.out.println(Acolor.BBLUE.get() + "CHEATS ACTIVE! " + this.name + Acolor.RESET.get());
    }
}
