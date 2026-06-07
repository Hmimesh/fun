package com.hmimesh.game;

/**
 * Player feats — passive abilities unlocked on level up or by defeating bosses.
 *
 * <p>Each feat has a name, description, category tag, and rarity (1 = Common,
 * 5 = Legendary).  The rarity controls how often a feat appears in the
 * weighted selection pool: common feats are offered more often than legendary
 * ones.
 *
 * <p>Call {@link #apply(Player)} to attach the feat's effect to the player,
 * and {@link #display()} to print it to the console with colour-coded rarity.
 *
 * @author Ben Farjun
 */
enum Feat {

    POWER("POWER", "Bonus damage +1", "damage", 1) {
        @Override public void apply(Player p) {
            p.setModifier(p.getModifier() + 1);
            System.out.println(Acolor.PURPLE.get() + "You gained POWER! Damage bonus +1" + Acolor.RESET.get());
        }
    },
    TANK("TANK", "Max HP +10", "health", 1) {
        @Override public void apply(Player p) {
            p.setMaxHP(p.getMaxHP() + 10);
            p.setHp(p.getHp() + 10);
            System.out.println(Acolor.CYAN.get() + "You gained TANK! Max HP +10" + Acolor.RESET.get());
        }
    },
    LUCK("LUCK", "Chance +10%", "luck", 3) {
        @Override public void apply(Player p) {
            p.setLuck(p.getLuck() + 10);
            System.out.println(Acolor.YELLOW.get() + "You gained LUCK! Chance +10%" + Acolor.RESET.get());
        }
    },
    DODGE("DODGE", "AC +1 (Armor Class)", "defense", 1) {
        @Override public void apply(Player p) {
            p.setAc(p.getAc() + 1);
            System.out.println(Acolor.GREEN.get() + "You gained DODGE! AC +1" + Acolor.RESET.get());
        }
    },
    DICER("DICER", "One more for attack dice", "damage", 5) {
        @Override public void apply(Player p) {
            p.setDiceCount(p.getDiceCount() + 1);
            System.out.println(Acolor.ORANGE.get() + "You gained DICER! One more for attack dice" + Acolor.RESET.get());
        }
    },
    RICH("RICH", "Get 300 gold", "gold", 1) {
        @Override public void apply(Player p) {
            p.setGold(p.getGold() + 300);
            System.out.println(Acolor.BGREEN.get() + "You gained RICH! 300 gold" + Acolor.RESET.get());
        }
    },
    ALCHEMIST("ALCHEMIST", "Better potions", "health", 2) {
        @Override public void apply(Player p) {
            p.setPotionHeal(p.getPotionHeal() + p.getMaxHP() / 10);
            System.out.println(Acolor.BGREEN.get() + "You gained ALCHEMIST! Better potions" + Acolor.RESET.get());
        }
    },
    FIREMAGIC("FIRE MAGIC", "Add 1d6 of fire damage", "damage", 5) {
        @Override public void apply(Player p) {
            p.setFireMage(true);
            p.dicePool();
            p.setFireMage(false);
            System.out.println(Acolor.BRED.get() + "You gained FIRE MAGIC! Add 1d6 of fire damage" + Acolor.RESET.get());
        }
    },
    POISON("POISON", "Add a chance of 1d4 of poison damage", "damage", 3) {
        @Override public void apply(Player p) {
            p.setPoisonMage(true);
            p.setCanPoison(true);
            p.dicePool();
            p.setPoisonMage(false);
            System.out.println(Acolor.BGREEN.get() + "You gained POISON! Add a consistent 1d4 of poison damage" + Acolor.RESET.get());
        }
    },
    WATERMAGE("Water Mage", "Add 1d8 of water damage", "damage", 5) {
        @Override public void apply(Player p) {
            p.setWaterMage(true);
            p.dicePool();
            p.setWaterMage(false);
            System.out.println(Acolor.BLUE.get() + "You gained WATER MAGE! Add 1d8 of water damage" + Acolor.RESET.get());
        }
    },
    RECOVERY("Recovery", "heal 10% of max hp at the end of battles", "health", 2) {
        @Override public void apply(Player p) {
            p.setRecovery(p.getRecovery() + 10);
            System.out.println(Acolor.RED.get() + "You gained RECOVERY! heal 10% of max hp at the end of battles" + Acolor.RESET.get());
        }
    };

    // ─── Fields ─────────────────────────────────────────────────────────────

    String name;
    String description;
    String category;
    int    rarity;

    // ─── Constructor ────────────────────────────────────────────────────────

    Feat(String name, String description, String category, int rarity) {
        this.name        = name;
        this.description = description;
        this.category    = category;
        this.rarity      = rarity;
    }

    // ─── Abstract ───────────────────────────────────────────────────────────

    /**
     * Applies this feat's stat effect to the given player.
     *
     * @param p the player to modify
     */
    public abstract void apply(Player p);

    // ─── Helpers ────────────────────────────────────────────────────────────

    /**
     * Prints this feat to the console with colour-coded rarity label.
     */
    public void display() {
        String rName;
        switch (rarity) {
            case 2:  rName = Acolor.BLUE.get()   + "Uncommon"  + Acolor.RESET.get(); break;
            case 3:  rName = Acolor.GREEN.get()  + "Rare"      + Acolor.RESET.get(); break;
            case 4:  rName = Acolor.YELLOW.get() + "Epic"      + Acolor.RESET.get(); break;
            case 5:  rName = Acolor.PURPLE.get() + "Legendary" + Acolor.RESET.get(); break;
            default: rName = "Common";
        }
        System.out.println(Acolor.PURPLE.get() + name + Acolor.RESET.get()
                + " - " + description + " (" + rName + ")");
    }
}
