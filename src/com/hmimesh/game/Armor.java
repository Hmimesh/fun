package com.hmimesh.game;

import java.util.Random;

/**
 * Armor piece worn by the player.
 *
 * <p>Contributes to the player's Armor Class (AC).  Call
 * {@link #updateArmor(Player)} to randomly generate a piece scaled to the
 * player's current level and luck.
 *
 * @author Ben Farjun
 */
class Armor {

    private String name;
    private int ac;
    private int price;
    private final Random rand = new Random();

    /**
     * Randomly selects an armor type and optionally enhances it based on the
     * player's level and luck.
     *
     * @param player the current player (provides level and luck for scaling)
     */
    public void updateArmor(Player player) {
        int chance = rand.nextInt(6) + 1;
        if (chance == 1) {
            this.name  = "leather";
            this.ac    = 1;
            this.price = 15;
        } else if (chance == 2) {
            this.name  = "chainmail";
            this.ac    = 2;
            this.price = 30;
        } else if (chance == 3) {
            this.name  = "plate";
            this.ac    = 3;
            this.price = 45;
        } else if (chance == 4) {
            this.name  = "mithril";
            this.ac    = 4;
            this.price = 60;
        } else if (chance == 5) {
            this.name  = "adamantium";
            this.ac    = 5;
            this.price = 75;
        } else {
            this.name  = "robe";
            this.ac    = 0;
            this.price = 5;
        }
        enhancerArm(player.getLvl(), player.getLuck());
    }

    // ─── Getters ────────────────────────────────────────────────────────────

    public String getName()  { return this.name;  }
    public int    getAc()    { return this.ac;    }
    public int    getPrice() { return this.price; }

    // ─── Private helpers ────────────────────────────────────────────────────

    /**
     * Applies random enhancement bonuses (AC and price) based on player level
     * and luck.  Higher level and luck make better enhancements more likely.
     *
     * @param playerlvl the player's level
     * @param luck      the player's luck stat
     */
    private void enhancerArm(int playerlvl, int luck) {
        int chance = rand.nextInt(101) + luck;
        if (playerlvl > 3) {
            if (chance >= 50 && chance < 70) {
                this.name  += "+1";
                this.ac    += 1;
                this.price += (int) (this.price * 0.30);
            } else if (chance >= 70 && chance < 80) {
                this.name  += "+2";
                this.ac    += 2;
                this.price += (int) (this.price * 0.50);
            } else if (chance >= 80 && chance < 90) {
                this.name  = Acolor.GREEN.get() + "Epic " + Acolor.RESET.get() + this.name;
                this.ac    += 3;
                this.price += (int) (this.price * 0.70);
            } else if (chance >= 90 && chance < 100 && playerlvl > 5) {
                this.name  = Acolor.PURPLE.get() + "Masterwork " + Acolor.RESET.get() + this.name;
                this.ac    += 4;
                this.price += (int) (this.price * 0.90);
            } else if (chance == 100 && playerlvl > 6) {
                this.name  = Acolor.CYAN.get() + "Legendary " + Acolor.RESET.get() + this.name;
                this.ac    += 5;
                this.price += this.price;
            }
        } else if (playerlvl < 3) {
            if (chance > 93) {
                this.name  = Acolor.GREEN.get() + "Epic " + Acolor.RESET.get() + this.name;
                this.ac    += 3;
                this.price += (int) (this.price * 0.50);
            } else if (chance > 87) {
                this.name  += "+2";
                this.ac    += 2;
                this.price += (int) (this.price * 0.50);
            } else if (chance > 80) {
                this.name  += "+1";
                this.ac    += 1;
                this.price += (int) (this.price * 0.30);
            }
        }
    }
}
