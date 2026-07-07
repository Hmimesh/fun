package com.hmimesh.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * The player character.
 *
 * <p>Extends {@link Entity} with player-specific state: race, weapon, armour,
 * inventory (bag), feats, dice pools for elemental magic, and potion healing.
 *
 * <p>Call {@link #newPlayer(String)} to initialise a fresh run.  The player's
 * race is chosen randomly from a pool (Human, Elf, Dwarf, Gnome), each with
 * small stat modifiers.
 *
 * <p>Feat selection is currently stored as pending and consumed via
 * {@link #offerFeatSelectionWithInput(Scanner)} — the GUI wiring for this is
 * a planned future improvement.
 *
 * @author Ben Farjun
 */
class Player extends Entity {

    private int     MAXHP;
    private int     MAXAC;
    private int     modifier;
    private Weapon  wep;
    private String  race;

    // ── Feat / magic booleans ────────────────────────────────────────────────
    private boolean fireMage   = false;
    private boolean waterMage  = false;
    private boolean poisonMage = false;
    private boolean canPoison  = false;

    // ── Combat / inventory state ─────────────────────────────────────────────
    private int  potionHeal;
    private int  diceCount;
    private int  recovery;
    private int  attack;
    private Item item;
    private Armor arm;

    private final Map<String, Integer>         bag       = new HashMap<>();
    private final Map<Feat, Integer>           feats     = new HashMap<>();
    private final Map<Integer, Integer>        dicepool  = new HashMap<>();
    private final Map<Integer, Integer>        poisonpool= new HashMap<>();
    private final List<String>                 racePool  = new ArrayList<>();
    private final Map<String, Map<String,Integer>> raceBonus = new HashMap<>();
    private final Random rand = new Random();

    // ── Pending feat slots (filled on level-up, consumed by input) ───────────
    private Feat[] pendingFeat    = new Feat[2];
    private Feat[] bossPendingFeat= new Feat[3];

    // ─── Constructor ────────────────────────────────────────────────────────

    public Player() {
        racePool.add("Human");
        racePool.add("Elf");
        racePool.add("Dwarf");
        racePool.add("Gnome");

        Map<String, Integer> humanBonus = new HashMap<>();
        humanBonus.put("hp", 0); humanBonus.put("ac", 0);
        humanBonus.put("modifier", 1); humanBonus.put("luck", 0);

        Map<String, Integer> elfBonus = new HashMap<>();
        elfBonus.put("hp", -5); elfBonus.put("ac", 1);
        elfBonus.put("modifier", 0); elfBonus.put("luck", 0);

        Map<String, Integer> dwarfBonus = new HashMap<>();
        dwarfBonus.put("hp", 5); dwarfBonus.put("ac", -1);
        dwarfBonus.put("modifier", 2); dwarfBonus.put("luck", -1);

        Map<String, Integer> gnomeBonus = new HashMap<>();
        gnomeBonus.put("hp", -5); gnomeBonus.put("ac", 0);
        gnomeBonus.put("modifier", -1); gnomeBonus.put("luck", 1);

        raceBonus.put("Human", humanBonus);
        raceBonus.put("Elf",   elfBonus);
        raceBonus.put("Dwarf", dwarfBonus);
        raceBonus.put("Gnome", gnomeBonus);
    }

    // ─── Initialisation ─────────────────────────────────────────────────────

    /**
     * Initialises all stats for a fresh game run.
     * Called after the player types their name.
     *
     * @param newName the player's chosen name
     */
    public void newPlayer(String newName) {
        this.setModifier(0);
        this.setPotionHeal(0);
        this.setDiceCount(0);
        this.setRecovery(0);
        this.setAttack(0);

        this.setFireMage(false);
        this.setWaterMage(false);
        this.setPoisonMage(false);
        this.setCanPoison(false);

        this.bag.clear();
        this.feats.clear();
        this.dicepool.clear();
        this.poisonpool.clear();

        this.setName(newName);
        this.setLvl(1);
        this.setXp(0);
        this.setRace(racePool);
        this.MAXHP = rand.nextInt(6) + 1 + 40;
        this.setHp(this.MAXHP);
        this.setLuck(0);

        this.arm = new Armor();
        this.diceCount = 0;
        this.arm.updateArmor(this);
        this.setAc(10 + this.arm.getAc());

        this.wep  = new Weapon();
        this.item = new Item();
        wep.update(rand.nextInt(6) + 1, this);

        int goldBound = 100 - this.arm.getPrice() + this.wep.getPrice();
        this.setGold(rand.nextInt(Math.max(1, goldBound)) + this.wep.getPrice() + 26);

        item.makePotion();
        this.bag.put(this.item.getName(), bag.getOrDefault(this.item.getName(), 0) + 1);
        this.attack = dmg();

        // Secret cheat codes
        if (newName.equals("GOD")) {
            Cheats.GOD.active(this);
            Cheats.GOD.display();
            this.setHp(this.MAXHP);
        }
        if (newName.equals("SEND HELP")) {
            Cheats.SENDHELP.active(this);
            Cheats.SENDHELP.display();
            this.setHp(this.MAXHP);
        }

        // Apply race bonuses
        Map<String, Integer> bonuses = raceBonus.get(this.getRace());
        this.setMaxHP(this.getMaxHP() + bonuses.getOrDefault("hp", 0));
        this.setHp(this.getMaxHP());
        this.setAc(this.getAc() + bonuses.getOrDefault("ac", 0));
        this.setModifier(this.getModifier() + bonuses.getOrDefault("modifier", 0));
        this.setLuck(this.getLuck() + bonuses.getOrDefault("luck", 0));
    }

    // ─── Getters ────────────────────────────────────────────────────────────

    public int     getMaxHP()      { return this.MAXHP;      }
    public int     getMaxAC()      { return this.MAXAC;      }
    public int     getModifier()   { return this.modifier;   }
    public Weapon  getWeapon()     { return this.wep;        }
    public boolean isFireMage()    { return this.fireMage;   }
    public boolean isWaterMage()   { return this.waterMage;  }
    public boolean isPoisonMage()  { return this.poisonMage; }
    public boolean canCastPoison() { return this.canPoison;  }
    public int     getPotionHeal() { return this.potionHeal; }
    public int     getDiceCount()  { return this.diceCount;  }
    public int     getRecovery()   { return this.recovery;   }
    public int     getAttack()     { return this.attack;     }
    public Item    getItem()       { return this.item;       }
    public Armor   getArmor()      { return this.arm;        }
    public Map<String, Integer> getBag()   { return this.bag;   }
    public Map<Feat, Integer>   getFeats() { return this.feats; }
    public String  getRace()       { return this.race;       }

    // ─── Setters ────────────────────────────────────────────────────────────

    public void setMaxHP(int maxhp)           { this.MAXHP      = maxhp;      }
    public void setMaxAC(int maxac)           { this.MAXAC      = maxac;      }
    public void setModifier(int modifier)     { this.modifier   = modifier;   }
    public void setWeapon(Weapon wep)         { this.wep        = wep;        }
    public void setFireMage(boolean b)        { this.fireMage   = b;          }
    public void setWaterMage(boolean b)       { this.waterMage  = b;          }
    public void setPoisonMage(boolean b)      { this.poisonMage = b;          }
    public void setCanPoison(boolean b)       { this.canPoison  = b;          }
    public void setPotionHeal(int potionHeal) { this.potionHeal = potionHeal; }
    public void setDiceCount(int diceCount)   { this.diceCount  = diceCount;  }
    public void setRecovery(int recovery)     { this.recovery   = recovery;   }
    public void setAttack(int attack)         { this.attack     = attack;     }
    public void setItem(Item item)            { this.item       = item;       }
    public void setArmor(Armor arm)           { this.arm        = arm;        }

    /** Sets the player's race by picking randomly from the given pool. */
    public void setRace(List<String> racePool) {
        this.race = racePool.get(rand.nextInt(racePool.size()));
    }

    // ─── XP / level ─────────────────────────────────────────────────────────

    /**
     * Returns the XP required to reach the next level.
     *
     * @return XP threshold for the next level-up
     */
    public int xpNeeded() {
        return (this.getLvl() + 1) * (this.getLvl() + 2) * 5;
    }

    // ─── Damage ─────────────────────────────────────────────────────────────

    /**
     * Adds a die to the elemental dice pool (fire d6, water d8) or the poison
     * pool (d4), depending on which mage flag is currently set.
     * Called by feat application methods.
     */
    public void dicePool() {
        if (this.fireMage) {
            this.dicepool.put(6, this.dicepool.getOrDefault(6, 0) + 1);
        } else if (this.waterMage) {
            this.dicepool.put(8, this.dicepool.getOrDefault(8, 0) + 1);
        } else if (this.poisonMage) {
            this.poisonpool.put(4, this.poisonpool.getOrDefault(4, 0) + 1);
        }
    }

    /**
     * Rolls all poison dice and returns the total poison damage for one tick.
     *
     * @return total poison damage
     */
    public int poisondmg() {
        int poisondmg = 0;
        for (Map.Entry<Integer, Integer> entry : poisonpool.entrySet()) {
            int diceType  = entry.getKey();
            int diceCount = entry.getValue();
            for (int i = 0; i < diceCount; i++) {
                poisondmg += rand.nextInt(diceType) + 1;
            }
        }
        return poisondmg;
    }

    /**
     * Returns a display string for the elemental dice pool, e.g. {@code "1d6 1d8"}.
     *
     * @return formatted dice pool string, or {@code ""} if the pool is empty
     */
    public String dicePoolToString() {
        if (this.dicepool.isEmpty()) return "";
        List<String> parts = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : dicepool.entrySet()) {
            int diceType  = entry.getKey();
            int diceCount = entry.getValue();
            String diceStr;
            switch (diceType) {
                case 6:  diceStr = Acolor.RED.get()  + "d6" + Acolor.RESET.get(); break;
                case 8:  diceStr = Acolor.BLUE.get() + "d8" + Acolor.RESET.get(); break;
                default: diceStr = "";
            }
            parts.add(diceCount + diceStr);
        }
        return String.join(" ", parts);
    }

    /**
     * Rolls all extra elemental dice and prints each result.
     *
     * @return total extra damage from elemental dice
     */
    private int rollExtraDice() {
        int extraDamage = 0;
        for (Map.Entry<Integer, Integer> entry : dicepool.entrySet()) {
            int diceType  = entry.getKey();
            int diceCount = entry.getValue();
            for (int i = 0; i < diceCount; i++) {
                int roll = rand.nextInt(diceType) + 1;
                extraDamage += roll;
                switch (diceType) {
                    case 6: System.out.println(Acolor.RED.get()  + "Fire damage: "  + roll + Acolor.RESET.get()); break;
                    case 8: System.out.println(Acolor.BLUE.get() + "Water damage: " + roll + Acolor.RESET.get()); break;
                    default: System.out.println("[DEBUG] Unknown damage type: " + roll);
                }
            }
        }
        return extraDamage;
    }

    /** {@inheritDoc} Also adds the weapon and modifier bonuses. */
    @Override
    public int hitBonus() {
        return this.modifier + this.wep.getBonus();
    }

    /**
     * Calculates total attack damage for one strike: weapon dice + extra dice
     * from feats + hit bonus.
     *
     * @return total damage dealt by this attack
     */
    @Override
    public int dmg() {
        int damage = 0;
        for (int i = 0; i < this.wep.getDiceCount() + this.diceCount; i++) {
            damage += rand.nextInt(this.wep.getDiceType()) + 1;
        }
        damage += rollExtraDice();
        damage += hitBonus();
        return damage;
    }

    // ─── Health helpers ──────────────────────────────────────────────────────

    /** Returns {@code true} if HP is at or below 45 % of max. */
    public boolean isLowHealth() {
        return this.getHp() > 0 && this.getHp() <= (int) (this.MAXHP * 0.45);
    }

    /** Returns {@code true} if the bag contains at least one healing potion. */
    public boolean hasAnyPotion() {
        return bag.getOrDefault("weak potion",   0) > 0
            || bag.getOrDefault("medium potion", 0) > 0
            || bag.getOrDefault("strong potion", 0) > 0
            || bag.getOrDefault("super potion",  0) > 0;
    }

    /**
     * Uses the strongest available potion, removes it from the bag, and heals
     * the player.
     *
     * @return name of the potion used, or {@code null} if none were available
     */
    public String useBestPotion() {
        String[] priorities = {"super potion", "strong potion", "medium potion", "weak potion"};
        int[]    heals      = {
            (int) (this.MAXHP * 0.7 + this.potionHeal),
            (int) (this.MAXHP * 0.5 + this.potionHeal),
            (int) (this.MAXHP * 0.3 + this.potionHeal),
            (int) (this.MAXHP * 0.15 + this.potionHeal)
        };
        for (int i = 0; i < priorities.length; i++) {
            String potion = priorities[i];
            int count     = bag.getOrDefault(potion, 0);
            if (count > 0) {
                bag.put(potion, count - 1);
                this.setHp(Math.min(this.getHp() + heals[i], this.MAXHP));
                return potion;
            }
        }
        return null;
    }

    /**
     * Called each combat round: auto-drinks the best available potion when HP
     * is low, or uses a revive if HP reaches 0.
     *
     * @return {@code true} if a potion was consumed
     */
    public boolean autoHealInFight() {
        if (this.isLowHealth()) {
            return this.hasAnyPotion();
        }
        if (this.getHp() <= 0 && this.bag.getOrDefault("revive", 0) > 0) {
            this.setHp(this.MAXHP / 2);
            this.bag.put("revive", this.bag.get("revive") - 1);
        }
        return false;
    }

    /**
     * Heals the player for {@link #recovery} % of max HP after a battle.
     * Prints the result to the console.
     */
    public int recoverAfterBattle() {
        if (this.getHp() < this.MAXHP) {
            int heal = (int) (this.MAXHP * (this.recovery / 100.0));
            this.setHp(Math.min(this.getHp() + heal, this.MAXHP));
            if (heal == 0) {
                System.out.println("Current health: " + this.getHp() + "/" + this.MAXHP + ".");
            } else {
                System.out.println("You recovered " + heal + " HP after battle. HP is now "
                        + this.getHp() + "/" + this.MAXHP + ".");
            }
            return heal;
        }
        return 0;
    }

    // ─── Level-up ────────────────────────────────────────────────────────────

    /**
     * Checks if the player has enough XP to level up and applies the level-up
     * effects (HP increase, modifier scaling, feat selection).
     * Loops until XP no longer exceeds the threshold.
     */
    public void checkLvlUp(GameWindow game) {
        while (this.getXp() >= xpNeeded()) {
            this.setLvl(this.getLvl() + 1);
            game.print("LEVEL UP!  You are now level " + this.getLvl() + "!");
            this.MAXHP += rand.nextInt(10) + 1 + this.getLvl();
            this.setHp(this.MAXHP);
            game.print("Max HP is now: " + this.MAXHP + "  (fully restored)");
            this.modifier = this.getLvl() / 2;
            game.print("Modifier: " + this.modifier + "  |  AC: " + this.getAc());
            offerFeatSelection();   // picks two feats silently; GameEngine shows them
        }
    }

    // ─── Feat selection ──────────────────────────────────────────────────────

    /** Triggers feat selection from a Scanner (terminal flow). */
    public void chooseFeatureFromInput(Scanner scan) {
        offerFeatSelectionWithInput(scan);
    }

    /**
     * Builds a weighted feat list where rarer feats appear less often.
     * Weight = 6 - rarity (so Common/rarity-1 gets 5 slots, Legendary/rarity-5 gets 1).
     */
    private List<Feat> createWeightedFeatList() {
        List<Feat> weightedList = new ArrayList<>();
        for (Feat feat : Feat.values()) {
            int weight = 6 - feat.rarity;
            for (int i = 0; i < weight; i++) {
                weightedList.add(feat);
            }
        }
        return weightedList;
    }

    /**
     * Offers 2 randomly-chosen feats (weighted by rarity) and stores them as
     * pending.  The player must call {@link #offerFeatSelectionWithInput(Scanner)}
     * to actually pick one.
     */
    private void offerFeatSelection() {
        List<Feat> weightedFeats = createWeightedFeatList();
        Feat feat1 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        Feat feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        while (feat2 == feat1) {
            feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        }
        storePendingFeats(feat1, feat2);
    }

    private void storePendingFeats(Feat f1, Feat f2) {
        pendingFeat[0] = f1;
        pendingFeat[1] = f2;
    }

    // ── GUI-oriented feat helpers ──────────────────────────────────────────────

    /** Returns {@code true} when the player has feats waiting to be chosen. */
    public boolean hasPendingFeat() { return pendingFeat[0] != null; }

    /** Returns the pending feat at index 0 or 1. */
    public Feat getPendingFeat(int idx) { return pendingFeat[idx]; }

    /**
     * Applies the feat chosen by the player through the GUI and clears pending slots.
     *
     * @param choice "1" picks feat 0, anything else picks feat 1
     */
    public void applyPendingFeatByChoice(String choice) {
        if (pendingFeat[0] == null) return;
        applyFeat("1".equals(choice) ? pendingFeat[0] : pendingFeat[1]);
        pendingFeat[0] = null;
        pendingFeat[1] = null;
    }

    /**
     * Reads the player's feat choice (1 or 2) and applies it.
     *
     * @param scan Scanner for player input
     */
    public void offerFeatSelectionWithInput(Scanner scan) {
        if (pendingFeat[0] == null) return;
        System.out.println("\nWhich feat do you choose? (1 or 2)");
        String choice = scan.next();
        if (choice.equals("1")) {
            applyFeat(pendingFeat[0]);
        } else if (choice.equals("2")) {
            applyFeat(pendingFeat[1]);
        } else {
            System.out.println("Invalid choice! Choosing feat 1...");
            applyFeat(pendingFeat[0]);
        }
        pendingFeat[0] = null;
        pendingFeat[1] = null;
    }

    /**
     * Offers 3 feats as a boss-kill reward (same weighted pool, 3 unique choices).
     */
    public void offerBossFeat() {
        List<Feat> weightedFeats = createWeightedFeatList();
        Feat feat1 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        Feat feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        while (feat2 == feat1) feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        Feat feat3 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        while (feat3 == feat1 || feat3 == feat2) feat3 = weightedFeats.get(rand.nextInt(weightedFeats.size()));

        System.out.println(Acolor.CYAN.get() + "\n===== BOSS DEFEATED! FEAT REWARD =====" + Acolor.RESET.get());
        System.out.println("You defeated a boss! Choose one feat:");
        System.out.println("Option 1: "); feat1.display();
        System.out.println("\nOption 2: "); feat2.display();
        System.out.println("\nOption 3: "); feat3.display();
        storeBossFeat(feat1, feat2, feat3);
    }

    private void storeBossFeat(Feat f1, Feat f2, Feat f3) {
        bossPendingFeat[0] = f1;
        bossPendingFeat[1] = f2;
        bossPendingFeat[2] = f3;
    }

    /**
     * Reads the player's boss-feat choice (1–3) and applies it.
     *
     * @param scan Scanner for player input
     */
    public void chooseBossFeat(Scanner scan) {
        if (bossPendingFeat[0] == null) return;
        System.out.println("\nWhich feat do you choose? (1, 2, or 3)");
        String choice = scan.next();
        Feat chosen;
        if (choice.equals("2"))      chosen = bossPendingFeat[1];
        else if (choice.equals("3")) chosen = bossPendingFeat[2];
        else {
            if (!choice.equals("1")) System.out.println("Invalid choice! Choosing feat 1...");
            chosen = bossPendingFeat[0];
        }
        applyFeat(chosen);
        bossPendingFeat[0] = bossPendingFeat[1] = bossPendingFeat[2] = null;
    }

    private void applyFeat(Feat feat) {
        feat.apply(this);
        this.feats.put(feat, this.feats.getOrDefault(feat, 0) + 1);
    }

    /** Prints all acquired feats to the console. */
    public void displayFeats() {
        if (feats.isEmpty()) {
            System.out.println("No feats acquired yet.");
            return;
        }
        System.out.println(Acolor.PURPLE.get() + "Your Feats:" + Acolor.RESET.get());
        for (Feat f : feats.keySet()) {
            System.out.println("  - " + f.name + " (x" + feats.get(f) + ")");
        }
    }

    // ─── Final boss trigger ──────────────────────────────────────────────────

    /**
     * Checks whether the current enemy should be replaced by the final boss.
     * This happens when the player is level 9 and the enemy's XP would push
     * them past the level-10 threshold.
     *
     * @param enemy the enemy that was generated for this fight
     * @return the same enemy, possibly reconfigured as the final boss
     */
    public Enemy finalBossFight(Enemy enemy) {
        if (this.getLvl() == 9 && (this.xpNeeded() - this.getXp()) <= enemy.getXp()) {
            enemy.finalBoss(this);
        }
        return enemy;
    }
}
