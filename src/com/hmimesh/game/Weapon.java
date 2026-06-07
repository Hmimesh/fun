package com.hmimesh.game;

import java.util.Random;

/**
 * A weapon held by the player.
 *
 * <p>Call {@link #update(int, Player)} to configure the weapon from a damage
 * seed value.  The weapon type, dice pool, bonus, and price are all derived
 * from that seed plus the player's current level and luck.
 *
 * @author Ben Farjun
 */
class Weapon {

    private String name;
    private int diceCount;
    private int diceType;
    private int data;    // seed used to select weapon tier
    private int bonus;
    private int price;

    /**
     * Returns a human-readable description of this weapon, e.g.
     * {@code "Sword 1d8+2"}.
     */
    @Override
    public String toString() {
        return this.name + " " + this.diceCount + "d" + this.diceType + "+" + this.bonus;
    }

    /**
     * Configures this weapon from a damage seed value.
     * The seed selects the weapon tier; enhancements are then applied based
     * on the player's level and luck.
     *
     * @param dmg    damage seed (1–∞); higher values unlock stronger weapons
     * @param player the current player (provides level and luck for scaling)
     */
    public void update(int dmg, Player player) {
        this.data = dmg;
        if (this.data <= 4) {
            this.name      = "Dagger";
            this.diceCount = 1;
            this.diceType  = 4;
        } else if (this.data <= 7) {
            this.name      = "club";
            this.diceCount = 1;
            this.diceType  = 6;
        } else if (this.data <= 10) {
            this.name      = "Sword";
            this.diceCount = 1;
            this.diceType  = 8;
        } else if (this.data <= 12) {
            this.name      = "Battle Axe";
            this.diceCount = 1;
            this.diceType  = 12;
        } else {
            this.name      = "Dragon slayer";
            this.diceCount = 2;
            this.diceType  = 6;
        }
        this.price = this.data * 10;
        checkBonusDice(player.getLvl(), player.getLuck());
        enhancerWep(player.getLvl(), player.getLuck());
    }

    // ─── Getters ────────────────────────────────────────────────────────────

    public String getName()      { return this.name;      }
    public int    getDiceCount() { return this.diceCount; }
    public int    getDiceType()  { return this.diceType;  }
    public int    getBonus()     { return this.bonus;     }
    public int    getPrice()     { return this.price;     }

    // ─── Private helpers ────────────────────────────────────────────────────

    /**
     * Adds a flat attack bonus and optionally a quality prefix based on player
     * level and luck (e.g. "+1", "Epic", "Masterwork", "Legendary").
     */
    private void enhancerWep(int playerlvl, int luck) {
        Random rand   = new Random();
        int chance    = rand.nextInt(101);
        if (playerlvl > 3) {
            if (chance + luck >= 50 && chance < 70) {
                this.name  += "+1";
                this.bonus += 1;
                this.price += (int) (this.price * 0.20);
            } else if (chance + luck >= 70 && chance < 80) {
                this.name  += "+2";
                this.bonus += 2;
                this.price += (int) (this.price * 0.50);
            } else if (chance + luck >= 80 && chance < 90) {
                this.name  = Acolor.GREEN.get() + "Epic " + Acolor.RESET.get() + this.name;
                this.bonus += 3;
                this.price += (int) (this.price * 0.70);
            } else if (chance + luck >= 90 && chance < 100 && playerlvl > 5) {
                this.name  = Acolor.PURPLE.get() + "Masterwork " + Acolor.RESET.get() + this.name;
                this.bonus += 4;
                this.price += (int) (this.price * 0.90);
            } else if (chance + luck == 100 && playerlvl > 6) {
                this.name  = Acolor.CYAN.get() + "Legendary " + Acolor.RESET.get() + this.name;
                this.bonus += 5;
                this.diceCount += 1;
                this.price += this.price;
            }
        } else if (playerlvl < 3) {
            if (chance > 93) {
                this.name  = Acolor.GREEN.get() + "Epic " + Acolor.RESET.get() + this.name;
                this.bonus += 3;
                this.price += (int) (this.price * 0.70);
            } else if (chance > 87) {
                this.name  += "+2";
                this.bonus += 2;
                this.price += (int) (this.price * 0.50);
            } else if (chance > 80) {
                this.name  += "+1";
                this.bonus += 1;
                this.price += (int) (this.price * 0.20);
            }
        }
    }

    /**
     * Potentially doubles or triples the weapon's dice count for high-luck,
     * high-level players.
     */
    private void checkBonusDice(int playerLevel, int luck) {
        Random rand = new Random();
        int chance  = rand.nextInt(101);
        if (playerLevel > 5) {
            if (chance + luck == 100) {
                this.name      = Acolor.CYAN.get() + "Tripled " + Acolor.RESET.get() + this.name;
                this.diceCount *= 3;
                this.price     += this.price;
            } else if (chance + luck > 80) {
                this.name      = Acolor.PURPLE.get() + "Doubled " + Acolor.RESET.get() + this.name;
                this.diceCount *= 2;
                this.price     += (int) (this.price * 0.80);
            }
        } else if (playerLevel < 5) {
            if (chance + luck == 100) {
                this.name      = Acolor.PURPLE.get() + "Doubled " + Acolor.RESET.get() + this.name;
                this.diceCount *= 2;
                this.price     += (int) (this.price * 0.80);
            }
        }
    }
}
