package com.hmimesh.game;

/**
 * Abstract base class shared by {@link Player} and {@link Enemy}.
 *
 * <p>Holds the core combat statistics (HP, AC, level, XP, gold, luck) and the
 * abstract damage method that every entity must implement.
 *
 * @author Ben Farjun
 */
abstract class Entity {

    private String name;
    private int hp;
    private int ac;
    private int bonus;
    private int lvl;
    private int xp;
    private int gold;
    private int luck;

    // ─── Abstract ───────────────────────────────────────────────────────────

    /** Returns the damage dealt by one attack roll of this entity. */
    abstract int dmg();

    // ─── Combat helpers ─────────────────────────────────────────────────────

    /**
     * Returns the flat bonus added to every attack roll (hit bonus).
     * Subclasses may override this to include weapon or modifier bonuses.
     *
     * @return the entity's hit bonus
     */
    public int hitBonus() {
        return bonus;
    }

    // ─── Getters ────────────────────────────────────────────────────────────

    public String getName() { return this.name; }
    public int    getHp()   { return this.hp;   }
    public int    getAc()   { return this.ac;   }
    public int    getBonus(){ return this.bonus; }
    public int    getLvl()  { return this.lvl;  }
    public int    getXp()   { return this.xp;   }
    public int    getGold() { return this.gold; }
    public int    getLuck() { return this.luck; }

    // ─── Setters ────────────────────────────────────────────────────────────

    public void setName(String name)  { this.name  = name;  }
    public void setHp(int hp)         { this.hp    = hp;    }
    public void setAc(int ac)         { this.ac    = ac;    }
    public void setBonus(int bonus)   { this.bonus = bonus; }
    public void setLvl(int lvl)       { this.lvl   = lvl;  }
    public void setXp(int xp)         { this.xp    = xp;   }
    public void setGold(int gold)     { this.gold  = gold; }
    public void setLuck(int luck)     { this.luck  = luck; }
}
