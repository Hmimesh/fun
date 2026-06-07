package com.hmimesh.game;

import java.util.Map;
import java.util.Random;

/**
 * An enemy encountered during combat.
 *
 * <p>Call {@link #lvlBased(int, boolean)} to procedurally generate an enemy
 * appropriate to the player's current level, or {@link #finalBoss(Player)} to
 * configure the end-game boss.
 *
 * <p>Enemy type is chosen by HP thresholds (Slime → Wolf → Goblin → Dragon).
 * Any enemy can be promoted to Boss via {@link #isBoss(boolean)}.
 *
 * @author Ben Farjun
 */
class Enemy extends Entity {

    private final Random rand = new Random();
    private String  data;        // enemy type tag ("Slime", "Wolf", etc.)
    private int     attack;
    private int     diceCount;
    private int     diceType;
    private int     poisonCount;
    private boolean boss      = false;
    private boolean poisoned  = false;

    // ─── Boss logic ─────────────────────────────────────────────────────────

    /**
     * Attempts to promote this enemy to Boss status.
     * There is a 20 % base chance, or 100 % if {@code ending} is {@code true}.
     * Bosses receive bonus AC, dice, luck, HP, and gold/XP multipliers.
     * Very high-level bosses may further be promoted to Legendary.
     *
     * @param ending if {@code true} the promotion is forced (final encounter)
     * @return {@code true} if the enemy became a boss
     */
    public boolean isBoss(boolean ending) {
        int chance = rand.nextInt(100);
        if (chance <= 20 || ending) {
            this.boss = true;
            this.setName(this.getName() + Acolor.BRED.get() + " Boss" + Acolor.RESET.get());
            this.setGold(this.getGold() * 2);
            this.setXp(this.getXp() * 2);
            this.poisonCount = 0;
            this.setAc(this.getAc() + 2);
            this.diceCount  += 1;
            this.setLuck(this.getLuck() + 10);
            this.setHp(this.getHp() + 5 * ((this.getLvl() / 2) + 1));
            this.setBonus(this.getBonus() + rand.nextInt(5) + 1);
            // Legendary chance — only for high-level bosses
            if ((rand.nextInt(101) - (this.getLvl() * 5) < 10) && this.getLvl() > 6) {
                this.setName(Acolor.CYAN.get() + "Legendary " + Acolor.RESET.get() + this.getName());
                this.setBonus(this.getBonus() + 1);
                this.setAc(this.getAc() + 1);
                this.setHp(this.getHp() + 10);
            }
            return true;
        }
        return false;
    }

    // ─── Damage ─────────────────────────────────────────────────────────────

    /**
     * Rolls damage for one attack using this enemy's dice pool.
     *
     * @return total damage dealt
     */
    @Override
    public int dmg() {
        int damage = 0;
        for (int i = 0; i < this.diceCount; i++) {
            damage += rand.nextInt(this.diceType) + 1;
        }
        damage += this.getBonus();
        return damage;
    }

    // ─── Procedural generation ──────────────────────────────────────────────

    /**
     * Configures this enemy based on the player's level and chosen difficulty.
     *
     * <p>HP is randomised first, then the HP range determines the enemy type:
     * Slime (weakest) → Wolf → Goblin → Dragon (strongest, level 3+).
     * An edge-case fallback creates a weak Rat for unexpected HP values.
     *
     * @param lvl    the player's current level
     * @param strong {@code true} for the "hard door" enemy (harder, higher level)
     */
    public void lvlBased(int lvl, boolean strong) {
        int lucky = rand.nextInt(100) + 1;

        this.setHp(rand.nextInt(lvl * 20 - lvl * 5 + 1) + lvl * 5);

        if (strong) {
            this.setLvl(rand.nextInt(4) + lvl);
        } else {
            int minLvl = 1;
            this.setLvl(rand.nextInt(2) + 1);
            this.setHp(this.getHp() - (int) (this.getHp() * 0.30));
            if (this.getLvl() < minLvl) this.setLvl(minLvl);
        }

        if (lucky >= 100 - this.getLuck()) {
            this.setLuck(this.getLuck() + 1);
            this.setName(this.getName() + " Lucky");
        }

        int minXp    = lvl;
        int maxBonus = Math.max(1, this.getLvl() / 2);

        if (this.getHp() <= lvl * 8 && this.getHp() >= lvl * 5) {
            // ── Slime ────────────────────────────────────────────────────────
            this.setAc(4 + lvl);
            this.diceType  = 4;
            this.diceCount = 1 + (lvl / 4);
            this.setBonus(lvl / 3);
            this.poisonCount = 0;
            this.attack = dmg();
            this.setXp(this.getLvl() / 4 + rand.nextInt(5) + 1);
            this.setGold(rand.nextInt(10) + this.getXp());
            this.setName(Acolor.BLUE.get() + "Slime" + Acolor.RESET.get());
            this.data = "Slime";

        } else if (this.getHp() > lvl * 8 && this.getHp() <= 12 * lvl) {
            // ── Wolf ─────────────────────────────────────────────────────────
            this.setAc(rand.nextInt((7 + lvl) - (4 + lvl) + 1) + (4 + lvl));
            this.diceType  = 6;
            this.diceCount = 1 + (lvl / 4);
            this.setBonus(rand.nextInt(maxBonus) + 1);
            this.poisonCount = 0;
            this.attack = dmg();
            this.setXp(this.getLvl() / 3 + rand.nextInt(6) + 2);
            this.setGold(rand.nextInt(20) + this.getXp());
            this.setName(Acolor.RED.get() + "Wolf" + Acolor.RESET.get());
            this.data = "Wolf";

        } else if (this.getHp() > 12 * lvl && this.getHp() <= 17 * lvl) {
            // ── Goblin ───────────────────────────────────────────────────────
            this.setAc(rand.nextInt((8 + lvl) - (4 + lvl) + 1) + (4 + lvl));
            this.diceType  = (lvl <= 2) ? 6 : 8;
            this.diceCount = 1 + (lvl / 4);
            this.setBonus(rand.nextInt(maxBonus) + 3);
            this.poisonCount = 0;
            this.attack = dmg();
            this.setXp(this.getLvl() / 2 + rand.nextInt(7) + 3);
            this.setGold(rand.nextInt(40) + 1 + this.getXp());
            this.setName(Acolor.BGREEN.get() + "Goblin" + Acolor.RESET.get());
            this.data = "Goblin";

        } else if (this.getHp() > 17 * lvl && this.getHp() <= 20 * lvl && lvl >= 3) {
            // ── Dragon (level 3+) ────────────────────────────────────────────
            this.setAc(rand.nextInt((10 + lvl) - (6 + lvl) + 1) + (6 + lvl));
            this.diceType  = 6;
            this.diceCount = 2 + (lvl / 4);
            this.setBonus(lvl / 2 + 5);
            this.poisonCount = 0;
            this.attack = dmg();
            this.setXp(this.getLvl() + rand.nextInt(15) + 4);
            this.setGold(rand.nextInt(100) + 50);
            this.setName(Acolor.PURPLE.get() + "Dragon" + Acolor.RESET.get());
            this.data = "Dragon";

        } else {
            // ── Rat (edge-case fallback) ──────────────────────────────────────
            this.setHp(10);
            this.setAc(2 + lvl);
            this.diceType  = 4;
            this.diceCount = 1;
            this.poisonCount = 0;
            this.setBonus(1);
            this.attack = dmg();
            this.setXp(1);
            this.setGold(1);
            this.setName(Acolor.YELLOW.get() + "RAT" + Acolor.RESET.get());
            this.data = "Rat";
        }

        if (this.getXp() <= 0) this.setXp(minXp);
    }

    /**
     * Checks whether the player earns an item drop after defeating this enemy.
     * Drop chance scales with enemy type and player luck.
     * Boss enemies always drop at least one item.
     *
     * @param Uhp    player HP (unused placeholder for future scaling)
     * @param Ulvl   player level (unused placeholder for future scaling)
     * @param bag    the player's item bag to add the drop into
     * @param player the player (for luck stat)
     */
    public void dropChance(int Uhp, int Ulvl, Map<String, Integer> bag, Player player) {
        int baseChance = 20;
        switch (this.data) {
            case "Slime":  baseChance += 5;  break;
            case "Wolf":   baseChance += 10; break;
            case "Goblin": baseChance += 15; break;
            case "Dragon": baseChance += 20; break;
            default: break;
        }
        int totalChance = baseChance + player.getLuck();
        if (rand.nextInt(100) + 1 < totalChance) {
            Item drop = new Item();
            drop.makePotion();
            bag.put(drop.getName(), bag.getOrDefault(drop.getName(), 0) + 1);
            System.out.println("You found a " + drop.getName());
        } else if (this.boss) {
            Item drop = new Item();
            drop.makePotion();
            bag.put(drop.getName(), bag.getOrDefault(drop.getName(), 0) + 1);
            System.out.println("You found a " + drop.getName());
        }
    }

    /**
     * Configures this enemy as the final boss — a supercharged version of the
     * player's own doppelganger. Only called when the player is about to
     * reach level 10.
     *
     * @param player the current player (used for the boss name flavour text)
     */
    public void finalBoss(Player player) {
        this.setLvl(9);
        this.setAc(rand.nextInt(10) + 7);
        this.setHp(22 * 9);
        this.diceType  = 8;
        this.diceCount = 3;
        this.setBonus(5);
        this.poisonCount = 0;
        this.attack = dmg();
        this.setXp(33);
        this.setGold(1000);
        this.setLuck(10);
        this.data = "Final Boss";
        this.setBoss(true);

        // Apply boss modifiers on top of the base stats
        this.setGold(this.getGold() * 2);
        this.setXp(this.getXp() * 2);
        this.setAc(this.getAc() + 2);
        this.diceCount += 1;
        this.setLuck(this.getLuck() + 10);
        this.setHp(this.getHp() + 5 * ((this.getLvl() / 2) + 1));
        this.setBonus(this.getBonus() + rand.nextInt(5) + 1);

        this.setName(Acolor.BRED.get() + "Final Boss " + Acolor.RESET.get()
                + Acolor.CYAN.get() + "Evil " + player.getName() + Acolor.RESET.get());
    }

    // ─── Getters ────────────────────────────────────────────────────────────

    public String  getData()        { return this.data;        }
    public int     getAttack()      { return this.attack;      }
    public int     getDiceCount()   { return this.diceCount;   }
    public int     getDiceType()    { return this.diceType;    }
    public int     getPoisonCount() { return this.poisonCount; }
    public boolean isBossEnemy()    { return this.boss;        }
    public boolean isPoisoned()     { return this.poisoned;    }

    // ─── Setters ────────────────────────────────────────────────────────────

    public void setData(String data)          { this.data        = data;        }
    public void setAttack(int attack)         { this.attack      = attack;      }
    public void setDiceCount(int diceCount)   { this.diceCount   = diceCount;   }
    public void setDiceType(int diceType)     { this.diceType    = diceType;    }
    public void setPoisonCount(int count)     { this.poisonCount = count;       }
    public void setBoss(boolean boss)         { this.boss        = boss;        }
    public void setPoisoned(boolean poisoned) { this.poisoned    = poisoned;    }
}
