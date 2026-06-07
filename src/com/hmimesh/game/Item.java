package com.hmimesh.game;

import java.util.Random;

/**
 * Represents a consumable item — either a healing potion or a revive token.
 *
 * <p>Call {@link #makePotion()} to randomly configure the item before use.
 * Used by the {@link Shop}, by enemy drops, and in the player's starting bag.
 *
 * @author Ben Farjun
 */
class Item {

    private String name;
    private int price;
    private int heal;
    private final Random rand = new Random();

    /**
     * Randomly configures this item as one of the available potion tiers
     * or a rare revive token.
     *
     * <p>Probability breakdown (d20):
     * <ul>
     *   <li>1–4  → weak potion   (price 10, heal 4)</li>
     *   <li>5–12 → medium potion (price 25, heal 10)</li>
     *   <li>13–18 → strong potion (price 40, heal 15)</li>
     *   <li>19   → super potion  (price 50, heal 20)</li>
     *   <li>20   → revive        (price 120, restores to ½ max HP)</li>
     * </ul>
     */
    public void makePotion() {
        int chance = rand.nextInt(20) + 1;
        if (chance <= 4) {
            this.name  = "weak potion";
            this.price = 10;
            this.heal  = 4;
        } else if (chance <= 12) {
            this.name  = "medium potion";
            this.price = 25;
            this.heal  = 10;
        } else if (chance <= 18) {
            this.name  = "strong potion";
            this.price = 40;
            this.heal  = 15;
        } else if (chance == 19) {
            this.name  = "super potion";
            this.price = 50;
            this.heal  = 20;
        } else { // chance == 20
            this.name  = "revive"; // restores player to half max HP
            this.price = 120;
            this.heal  = 1;
        }
    }

    // ─── Getters ────────────────────────────────────────────────────────────

    public String getName()  { return this.name;  }
    public int    getPrice() { return this.price; }
    public int    getHeal()  { return this.heal;  }
}
