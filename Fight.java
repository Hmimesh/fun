import java.util.Scanner;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/** In this program we are doing a random to check who will win in a psudo very simplified dnd style combat
 * @Author Ben Farjun
 * @git.hub @Hmimesh
 * @version 30/03/2026
*/ 


//=================== COLOR ENUM =====================

class ColorConfig{
    public static final boolean USE_COLOR = System.console() != null;
}

enum Color{ // ANSI colors wrok only on more newer terminals, if wanna use it and see it please run on something like WSL / Powershell / Linux etc... and not on something like BlueJ
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    ORANGE("\u001B[38;5;208m"),
    BGREEN("\u001B[38;5;46m"),
    BRED("\u001B[38;5;196m"),
    RESET("\u001B[0m");

    private final String code;

    Color(String code){
        this.code = code;
    }

    public String get(){
        return ColorConfig.USE_COLOR ? code : "";
    }
}

// ================= ENTETY CLASS =================
abstract class Entity{
    private String name;
    private int hp;
    private int ac;
    private int bonus;
    private int lvl;
    private int xp;
    private int gold;
    private int luck; 

    abstract int dmg();

    public int hitBonus(){
       return bonus;
    }
    
    // ===== GETTERS =====
    public String getName(){
        return this.name;
    }
    
    public int getHp(){
        return this.hp;
    }
    
    public int getAc(){
        return this.ac;
    }
    
    public int getBonus(){
        return this.bonus;
    }
    
    public int getLvl(){
        return this.lvl;
    }
    
    public int getXp(){
        return this.xp;
    }
    
    public int getGold(){
        return this.gold;
    }
    
    public int getLuck(){
        return this.luck;
    }
    
    // ===== SETTERS =====
    public void setName(String name){
        this.name = name;
    }
    
    public void setHp(int hp){
        this.hp = hp;
    }
    
    public void setAc(int ac){
        this.ac = ac;
    }
    
    public void setBonus(int bonus){
        this.bonus = bonus;
    }
    
    public void setLvl(int lvl){
        this.lvl = lvl;
    }
    
    public void setXp(int xp){
        this.xp = xp;
    }
    
    public void setGold(int gold){
        this.gold = gold;
    }
    
    public void setLuck(int luck){
        this.luck = luck;
    }

}




//=================== ENEMY CLASS ====================

class Enemy extends Entity{ //the enemy class should be with hp for hit points, ac = for armor class, and attack for bonusus
    private Random rand = new Random();
    private String data;
    private int attack;
    private int diceCount;
    private int diceType;
    private int potionHeal;
    private int poisonCount;
    private boolean boss;
    private boolean poisoned = false;

    public boolean isBoss(boolean ending){
        int chance =  rand.nextInt(100);
        boolean isFinal = ending;
        if(chance <= 20 || isFinal == true ){
            this.boss = true;
            this.setName(this.getName() + Color.BRED.get() + " Boss" + Color.RESET.get());
            this.setGold(this.getGold() * 2);
            this.setXp(this.getXp() * 2);
            this.poisonCount = 0;
            this.setAc(this.getAc() + 2);
            this.diceCount += 1;
            this.setLuck(this.getLuck() + 10);
            this.setHp(this.getHp() + 5 * ((this.getLvl() / 2) + 1));
            this.setBonus(this.getBonus() + rand.nextInt(5) + 1);
            if((rand.nextInt((100) + 1) - (this.getLvl() * 5) < 10 ) && this.getLvl() > 6){
                this.setName(Color.CYAN.get() + "Legendary " + Color.RESET.get() + this.getName());
                this.setBonus(this.getBonus() + 1);
                this.setAc(this.getAc() + 1);
                this.setHp(this.getHp() + 10);
            }
            return true;
        }else{
            return false;
        }
    }

    public int dmg(){
        int damage = 0;
        Random rand = new Random();
        for (int i = 0; i < this.diceCount; i++){
            damage += rand.nextInt(this.diceType) + 1;
        }
        damage += this.getBonus();
        return damage;
    }

    public void lvlBased(int lvl){ //lvl is user lvl
        
        int strongOrWeak = rand.nextInt(10) + 1;
        int lucky = rand.nextInt(100) + 1;

        this.setHp(rand.nextInt(lvl * 20 - lvl * 5 + 1) + lvl * 5);
    
        if (strongOrWeak <= 5){
            this.setLvl(rand.nextInt(4) + lvl) ;
        }else{
            int minLvl = 1;
            this.setLvl(rand.nextInt(2) + 1);
            this.setHp(this.getHp() - (int)(this.getHp() * 0.30));
            if (this.getLvl() < minLvl){
                this.setLvl(minLvl);
            }
        }

        if (lucky >= 97){
            this.setLuck(1);
            this.setName(this.getName() + Color.GREEN.get() + " Lucky" + Color.RESET.get());
        }
        
        int minXp = lvl;
        

        int maxBonus = Math.max(1, this.getLvl() / 2);
        
        if(this.getHp() <= lvl * 8 && this.getHp() >= lvl * 5){
            this.setAc(4 + lvl);
            this.diceType = 4;
            this.diceCount = 1 + ((lvl / 4));
            this.setBonus(lvl / 3);
            this.poisonCount = 0;
            this.attack = dmg();
            this.setXp(this.getLvl() / 4 + rand.nextInt(5) + 1);
            this.setGold(rand.nextInt(10) + this.getXp());
            this.setName(Color.BLUE.get() + "Slime" + Color.RESET.get());
            this.data = "Slime";
        }
        else if(this.getHp() > lvl * 8 && this.getHp() <= 12 * lvl){
            this.setAc(rand.nextInt((7 + lvl) - (4 + lvl) + 1) + (4 + lvl));
            this.diceType = 6;
            this.diceCount = 1 + ((lvl / 4));
            this.setBonus(rand.nextInt((maxBonus)) + 1);
            this.poisonCount = 0;
            this.attack = dmg();
            this.setXp(this.getLvl() / 3 + rand.nextInt(6) + 2);
            this.setGold(rand.nextInt(20) + this.getXp());
            this.setName(Color.RED.get() + "Wolf" + Color.RESET.get());
            this.data = "Wolf";
        }
        else if(this.getHp() > 12 * lvl && this.getHp() <= 17 * lvl){
            this.setAc(rand.nextInt((8 + lvl) - (4 + lvl) + 1) + (4 + lvl));
            if(lvl <= 2){
                this.diceType = 6;
            }else{
                this.diceType = 8;
            }    
            this.diceCount = 1 + ((lvl / 4));
            this.setBonus(rand.nextInt(maxBonus) + 3);
            this.poisonCount = 0;
            this.attack = dmg();
            this.setXp(this.getLvl() / 2 + rand.nextInt(7) + 3);
            this.setGold(rand.nextInt(40) + 1 + this.getXp());
            this.setName(Color.BGREEN.get() + "Goblin" + Color.RESET.get());
            this.data = "Goblin";
        }
        else if(this.getHp() > 17 * lvl && this.getHp() <= 20 * lvl && lvl >= 3){
            this.setAc(rand.nextInt((10 + lvl) - (6 + lvl) + 1) + (6 + lvl));
            this.diceType = 6;
            this.diceCount = 2 + ((lvl / 4));
            this.setBonus(lvl / 2 + 5);
            this.poisonCount = 0;
            this.attack = dmg();
            this.setXp(this.getLvl() + rand.nextInt(15) +  4);
            this.setGold(rand.nextInt(100) + 50);
            this.setName(Color.PURPLE.get() + "Dragon" + Color.RESET.get());
            this.data = "Dragon";
        }else{
            this.setHp(10);
            this.setAc(2 + lvl);
            this.diceType = 4;
            this.diceCount = 1;
            this.poisonCount = 0;
            this.setBonus(1);
            this.attack = dmg();
            this.setXp(1);
            this.setGold(1);
            this.setName(Color.YELLOW.get() + "RAT" + Color.RESET.get());
            this.data = "Rat";
        }
        if (this.getXp() <= 0){
            this.setXp(minXp);
        }
    }

    public void dropChance(int Uhp, int Ulvl, Map<String, Integer> bag, Player player){ //item drops, potions will be called when enemy is dead
        int chance;
        if (this.data.equals("Slime")){
            chance = ((Uhp * 3) / Ulvl);
        }else if (this.data.equals("Wolf")){
            chance = ((Uhp * 5) / Ulvl);
        }else if (this.data.equals("Goblin")){
            chance = ((Uhp * 7) / Ulvl);
        }else if (this.data.equals("Rat")){
            chance = ((Uhp * 2) / Ulvl);
        }
        else{
            chance = (int)((Uhp * 10) / Ulvl) / 100;
        }

        //drop chance is 30% with out player luck modifier
        if (60 < (rand.nextInt(100) + chance + player.getLuck())){
            Item drop = new Item();
            drop.makePotion();
            bag.put(drop.getName(), bag.getOrDefault(drop.getName(), 0) + 1);
            System.out.println("You found a " + drop.getName());
        }else if(this.boss == true){ //if boss alwats get between 1 - 2 rewards
            Item drop = new Item();
            drop.makePotion();
            bag.put(drop.getName(), bag.getOrDefault(drop.getName(), 0) + 1);
            System.out.println("You found a " + drop.getName());
        }     
        
        
    }

    public void finalBoss(){
        this.setLvl(9);
        this.setAc(this.rand.nextInt(10) + 7);
        this.setHp(18 * 9);
        this.diceType = 8;
        this.diceCount = 2;
        this.setBonus(5);
        this.poisonCount = 0;
        this.attack = dmg();
        this.setXp(33); // max xp giving by the dragon + 1
        this.setGold(1000);
        this.setName("TACHO");
        this.data = "Final Boss";
        this.isBoss(true);
        String bossName = Color.BRED.get() + "Final " + Color.RESET.get() + Color.CYAN.get() + "TACHO" + Color.RESET.get();
        this.setName(bossName);
    }

    // ===== GETTERS =====
    public String getData(){
        return this.data;
    }
    
    public int getAttack(){
        return this.attack;
    }
    
    public int getDiceCount(){
        return this.diceCount;
    }
    
    public int getDiceType(){
        return this.diceType;
    }
    
    public int getPotionHeal(){
        return this.potionHeal;
    }
    
    public int getPoisonCount(){
        return this.poisonCount;
    }
    
    public boolean isBossEnemy(){
        return this.boss;
    }
    
    public boolean isPoisoned(){
        return this.poisoned;
    }
    
    // ===== SETTERS =====
    public void setData(String data){
        this.data = data;
    }
    
    public void setAttack(int attack){
        this.attack = attack;
    }
    
    public void setDiceCount(int diceCount){
        this.diceCount = diceCount;
    }
    
    public void setDiceType(int diceType){
        this.diceType = diceType;
    }
    
    public void setPotionHeal(int potionHeal){
        this.potionHeal = potionHeal;
    }
    
    public void setPoisonCount(int poisonCount){
        this.poisonCount = poisonCount;
    }
    
    public void setBoss(boolean boss){
        this.boss = boss;
    }
    
    public void setPoisoned(boolean poisoned){
        this.poisoned = poisoned;
    }
}
//============== WEAPON CLASS ==================

class Weapon{ // weapons and their abilities
    private String name;
    private int diceCount; //number of dices
    private int diceType; // type of dice
    private int data; // ID for the damage stats
    private int bonus;
    private int price;


    public void update(int dmg, int playerlvl, Player player){  // will be called when buying or first opening the game
        this.data = dmg; 
        if(this.data <= 4){
            this.name = "Dagger";
            this.diceCount = 1;
            this.diceType = 4;
        }
        else if(this.data >= 5 && this.data <= 7){
            this.name = "club";
            this.diceCount = 1;
            this.diceType = 6;
        }
        else if(this.data >= 8 && this.data <= 10){
            this.name = "Sword";
            this.diceCount = 1;
            this.diceType = 8;
        }
        else if(this.data > 10 && this.data <= 12){
            this.name = "Battle Axe";
            this.diceCount = 1;
            this.diceType = 12;
        }else{
            this.name = "Dragon slayer";
            this.diceCount = 2;
            this.diceType = 6;
        }
        this.price = this.data * 10;
        checkBonusDice(playerlvl, player.getLuck());
        enhancerWep(playerlvl, player.getLuck());
        

    }

    // ===== GETTERS =====
    public String getName(){
        return this.name;
    }
    
    public int getDiceCount(){
        return this.diceCount;
    }
    
    public int getDiceType(){
        return this.diceType;
    }
    
    public int getBonus(){
        return this.bonus;
    }
    
    public int getPrice(){
        return this.price;
    }

    private void enhancerWep(int playerlvl , int luck){
        Random rand = new Random();
        int chance = rand.nextInt((100) + 1);
        if(playerlvl > 3){
            if(chance + luck >= 50 && chance < 70){
                this.name += "+1";
                this.bonus += 1;
                this.price += (int)(this.price * (0.20));
            }else if(chance + luck >= 70 && chance < 80){
                this.name += "+2";
                this.bonus += 2;
                this.price += (int)(this.price * (0.50));
            }else if (chance + luck >= 80 && chance < 90){
                this.name = Color.GREEN.get() + "Epic " + Color.RESET.get() + this.name;
                this.bonus += 3;
                this.price += (int)(this.price * (0.70));
            }else if(chance + luck >= 90 && chance < 100 && playerlvl > 5){
                this.name = Color.PURPLE.get() + "Masterwork " + Color.RESET.get() + this.name;
                this.bonus += 4;
                this.price += (int)(this.price * (0.90));
            }else if (chance + luck == 100 && playerlvl > 6) {
                this.name = Color.CYAN.get() + "Legendary " + Color.RESET.get() + this.name;
                this.bonus += 5;
                this.diceCount += 1;
                this.price += this.price;
            }


        }else if(playerlvl < 3){
            if(chance > 93){
                this.name =Color.GREEN.get() + "Epic " + Color.RESET.get() + this.name;
                this.bonus += 3;
                this.price += (int)(this.price * (0.70));
            }else if(chance  > 87){
                this.name += "+2";
                this.bonus += 2;
                this.price += (int)(this.price * (0.50));
            } else if (chance > 80){
                this.name += "+1";
                this.bonus += 1;
                this.price += (int)(this.price * (0.20));
            }
        }
    } 

    private void checkBonusDice(int playerLevel, int luck){
        Random rand = new Random();
        int chance = rand.nextInt((100) + 1);

        if (playerLevel > 5){
            if (chance + luck == 100){
                this.name = Color.CYAN.get() + "Tripled " + Color.RESET.get() + this.name;
                this.diceCount *= 3;
                this.price += this.price;
            }else if (chance + luck > 80){
                this.name = Color.PURPLE.get() + "Doubled " + Color.RESET.get() + this.name;
                this.diceCount *= 2;
                this.price += (int)(this.price * (0.80));
            }
        
        }else if (playerLevel < 5){
            if (chance  + luck == 100){
                this.name = Color.PURPLE.get() + "Doubled " + Color.RESET.get() + this.name;
                this.diceCount *= 2;
                this.price += (int)(this.price * (0.80));
            }
        }   

    }
}

//=================== FEAT ENUM =====================

enum Feat{
    POWER("POWER", "Bonus damage +1", "damage", 1){
        public void apply(Player p){
            p.setModifier(p.getModifier() + 1);
            System.out.println(Color.PURPLE.get() + "You gained POWER! Damage bonus +1" + Color.RESET.get());
        }
    },
    TANK("TANK", "Max HP +10", "health",  1){
        public void apply(Player p){
            p.setMaxHP(p.getMaxHP() + 10);
            p.setHp(p.getHp() + 10);
            System.out.println(Color.CYAN.get() + "You gained TANK! Max HP +10" + Color.RESET.get());
        }
    },
    LUCK("LUCK", "Chance +10%", "luck", 3){
        public void apply(Player p){
            p.setLuck(p.getLuck() + 10);
            System.out.println(Color.YELLOW.get() + "You gained LUCK! Chance +10%" + Color.RESET.get());
        }
    },
    DODGE("DODGE", "AC +1 (Armor Class)", "defense", 1){
        public void apply(Player p){
            p.setAc(p.getAc() + 1);
            System.out.println(Color.GREEN.get() + "You gained DODGE! AC +1" + Color.RESET.get());
        }
    },
    DICER("DICER", "One more for attack dice", "damage",  5){
        public void apply(Player p){
            p.setDiceCount(p.getDiceCount() + 1);
            System.out.println(Color.ORANGE.get() + "You gained DICER! One more for attack dice" + Color.RESET.get());
        }
    },
    RICH("RICH", "Get 300 gold", "gold", 1){
        public void apply(Player p){
            p.setGold(p.getGold() + 300);
            System.out.println(Color.BGREEN.get() + "You gained RICH! 300 gold" + Color.RESET.get());
        }
    },
    ALCHEMIST("ALCHEMIST", "Better potions", "health", 2){
        public void apply(Player p){
            p.setPotionHeal(p.getPotionHeal() + p.getMaxHP() / 10);
            System.out.println(Color.BGREEN.get() + "You gained ALCHEMIST! Better potions" + Color.RESET.get());
        }
    },
    FIREMAGIC("FIRE MAGIC", "Add 1d6 of fire damage", "damage", 5){
        public void apply(Player p){
            p.setFireMage(true);
            p.dicePool();
            p.setFireMage(false);
            System.out.println(Color.BRED.get() + "You gained FIRE MAGIC! Add 1d6 of fire damage" + Color.RESET.get());
        }
    },
    POISON("POISON", "Add a chance of 1d4 of poison damage", "damage", 3){
        public void apply(Player p){
            p.setPoisonMage(true);
            p.setCanPoison(true);
            p.dicePool();
            p.setPoisonMage(false);
            System.out.println(Color.BGREEN.get() + "You gained POISON! Add a consistent 1d4 of poison damage" + Color.RESET.get());
        }
    },
    WATERMAGE("Water Mage", "Add 1d8 of water damage", "damage", 5){
        public void apply(Player p){
            p.setWaterMage(true);
            p.dicePool();
            p.setWaterMage(false);
            System.out.println(Color.BLUE.get() + "You gained WATER MAGE! Add 1d8 of water damage" + Color.RESET.get());
        }
    },
    RECOVERY("Recovery", "heal 10% of max hp and the end of battles", "health", 2){
        public void apply(Player p){
            p.setRecovery(p.getRecovery() + 10);
            System.out.println(Color.RED.get() + "You gained RECOVERY! heal 10% of max hp and the end of battles" + Color.RESET.get());
        }
    };

    String name;
    String description;
    String category;
    int rarity;

    Feat(String name, String description, String category, int rarity){
        this.name = name;
        this.description = description;
        this.category = category;
        this.rarity = rarity;
    
    }


    public abstract void apply(Player p);

    public void display(){
    String rName;
        switch(rarity){
        case 2:
            rName = Color.BLUE.get() + "Uncommon" + Color.RESET.get();
            break;
        case 3:
            rName = Color.GREEN.get() + "Rare" + Color.RESET.get();
            break;
        case 4:
            rName = Color.YELLOW.get() + "Epic" + Color.RESET.get();
            break;
        case 5:
            rName = Color.PURPLE.get() + "Legendary" + Color.RESET.get();
            break;
        default:
            rName = "Common";
       
}
    System.out.println(Color.PURPLE.get() + name + Color.RESET.get() + " - " + description +   "(" + rName + ")" );
}
}

//========== PLAYER CLAS ============

class Player extends Entity{
    private int MAXHP;
    private int MAXAC;
    private int modifier;
    private Weapon wep;
    // =========== Booleans feats ==========
    private boolean fireMage = false;
    private boolean waterMage = false;
    private boolean poisonMage = false;
    private boolean canPoison = false;

    // ========== Booleans feats end =========
    private int potionHeal;
    private int diceCount;
    private int recovery;
    private int attack;
    private Item item; // holder for potions for easy accsess for the bag
    private Armor arm;

    private Map<String, Integer> bag = new HashMap<>(); //bag for diff potions
    private Map<Feat, Integer> feats = new HashMap<>(); //track acquired feats and count
    private Map<Integer, Integer> dicepool = new HashMap<>();
    private Map<Integer, Integer> poisonpool= new HashMap<>();


    private Random rand = new Random();


    public void newPlayer(String newName){ //when starting new game init for the user
        this.setName(Color.ORANGE.get() + newName + Color.RESET.get());
        this.setLvl(1);
        this.setXp(0);
        this.MAXHP = rand.nextInt(6) + 1 + 40;
        this.setHp(this.MAXHP);
        this.setLuck(0);
        this.arm = new Armor();
        this.diceCount = 0;
        this.arm.updateArmor(this.getLvl(), this);
        this.setAc(10 + this.arm.getAc());
        this.wep = new Weapon();
        this.item = new Item();
        this.setGold(rand.nextInt(100 - (this.arm.getPrice()) + this.wep.getPrice()) + 26);    
        item.makePotion();
        this.bag.put(this.item.getName(), bag.getOrDefault(this.item.getName(), 0) + 1);
        wep.update(rand.nextInt(6) + 1, this.getLvl(), this);
        this.attack = dmg();
    }

    // ===== GETTERS =====
    public int getMaxHP(){
        return this.MAXHP;
    }
    
    public int getMaxAC(){
        return this.MAXAC;
    }
    
    public int getModifier(){
        return this.modifier;
    }
    
    public Weapon getWeapon(){
        return this.wep;
    }
    
    public boolean isFireMage(){
        return this.fireMage;
    }
    
    public boolean isWaterMage(){
        return this.waterMage;
    }
    
    public boolean isPoisonMage(){
        return this.poisonMage;
    }
    
    public boolean canCastPoison(){
        return this.canPoison;
    }
    
    public int getPotionHeal(){
        return this.potionHeal;
    }
    
    public int getDiceCount(){
        return this.diceCount;
    }
    
    public int getRecovery(){
        return this.recovery;
    }
    
    public int getAttack(){
        return this.attack;
    }
    
    public Item getItem(){
        return this.item;
    }
    
    public Armor getArmor(){
        return this.arm;
    }
    
    public Map<String, Integer> getBag(){
        return this.bag;
    }
    
    public Map<Feat, Integer> getFeats(){
        return this.feats;
    }
    
    // ===== SETTERS =====
    public void setMaxHP(int maxhp){
        this.MAXHP = maxhp;
    }
    
    public void setMaxAC(int maxac){
        this.MAXAC = maxac;
    }
    
    public void setModifier(int modifier){
        this.modifier = modifier;
    }
    
    public void setWeapon(Weapon wep){
        this.wep = wep;
    }
    
    public void setFireMage(boolean fireMage){
        this.fireMage = fireMage;
    }
    
    public void setWaterMage(boolean waterMage){
        this.waterMage = waterMage;
    }
    
    public void setPoisonMage(boolean poisonMage){
        this.poisonMage = poisonMage;
    }
    
    public void setCanPoison(boolean canPoison){
        this.canPoison = canPoison;
    }
    
    public void setPotionHeal(int potionHeal){
        this.potionHeal = potionHeal;
    }
    
    public void setDiceCount(int diceCount){
        this.diceCount = diceCount;
    }
    
    public void setRecovery(int recovery){
        this.recovery = recovery;
    }
    
    public void setAttack(int attack){
        this.attack = attack;
    }
    
    public void setItem(Item item){
        this.item = item;
    }
    
    public void setArmor(Armor arm){
        this.arm = arm;
    }

    public int xpNeeded(){ // for lvl ups
        int xpNeeded = (this.getLvl() + 1) * (this.getLvl() + 2) * 5;
        return xpNeeded;
    }
   // ========= DAMAGE METHODS ============ 
    public void dicePool(){
        if(this.fireMage == true){
            this.dicepool.put(6, this.dicepool.getOrDefault(6, 0) + 1);
        }else if(this.waterMage == true){
            this.dicepool.put(8, this.dicepool.getOrDefault(8, 0) + 1);
        }else if(this.poisonMage == true){
            this.poisonpool.put(4, this.poisonpool.getOrDefault(4, 0) + 1);
        }
    }
    
    public int poisondmg(){
        int poisondmg = 0;
        for(Map.Entry<Integer, Integer> entry: poisonpool.entrySet()){
            int diceType = entry.getKey();
            int diceCount = entry.getValue();
            for (int i = 0; i < diceCount; i++){
                poisondmg += rand.nextInt(diceType) + 1;
            }
        }
        return poisondmg;
    }

    public String dicePoolToString(){
        if(this.dicepool.isEmpty()){
            return "";
        }else{
        List<String> dicePoolStrings = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : dicepool.entrySet()){
            int diceType = entry.getKey();
            int diceCount = entry.getValue();
            String diceString = "";

            switch(diceType){
                case 6:
                    diceString = Color.RED.get() + "d6" + Color.RESET.get();
                    break;
                case 8:
                    diceString = Color.BLUE.get() + "d8" + Color.RESET.get();
                    break;
                default:
                    diceString = "";
            }
            dicePoolStrings.add(diceCount + diceString);  // Add once, outside inner loop
        }
        // Join the list into a single string
        return String.join(" ", dicePoolStrings);
        }
    }

    private int rollExtraDice(){
        int extraDamage = 0; 
        for(Map.Entry<Integer, Integer> entry : dicepool.entrySet()){
            int diceType = entry.getKey();
            int diceCount = entry.getValue();
            for(int i = 0; i < diceCount; i++){
                int roll = rand.nextInt(diceType) + 1;
                extraDamage += roll;
                switch(diceType){
                    case 6:
                        System.out.println(Color.RED.get() + "Fire damage: " + roll + Color.RESET.get());
                        break;
                    case 8:
                        System.out.println( Color.BLUE.get() +"Water damage: "  + roll + Color.RESET.get());
                        break;
                    default:
                        System.out.println("[DEBUG] Unknown damage type: " + roll);
                }
            }
        }
        return extraDamage;
    }

    @Override
    public int hitBonus() {
        return this.modifier + this.wep.getBonus();
    }


    public int dmg(){ // main damage algo
        int damage = 0;;
        for (int i = 0; i < this.wep.getDiceCount() + this.diceCount; i++){
            damage += rand.nextInt(this.wep.getDiceType()) + 1;
        }
        damage += rollExtraDice();
        damage += this.wep.getBonus() + this.modifier;
        return damage;
    }

    // =============== HEALTH METHODS ===============
    public boolean isLowHealth(){
        return this.getHp() > 0 && this.getHp() <= (int)(this.MAXHP * 0.45);
    }

    public boolean hasAnyPotion(){
        return bag.getOrDefault("weak potion", 0) > 0
            || bag.getOrDefault("medium potion", 0) > 0
            || bag.getOrDefault("strong potion", 0) > 0
            || bag.getOrDefault("super potion", 0) > 0;
    }

    private String useBestPotion(){
        String[] priorities = {"super potion", "strong potion", "medium potion", "weak potion"};
        int[] heals = {(int)(this.MAXHP * 0.7 + this.potionHeal), (int)(this.MAXHP * 0.5 + this.potionHeal), (int)(this.MAXHP * 0.3 + this.potionHeal), (int)(this.MAXHP * 0.15 + this.potionHeal)};

        for (int i = 0; i < priorities.length; i++){
            String potion = priorities[i];
            int count = bag.getOrDefault(potion, 0);
            if (count > 0){
                bag.put(potion, count - 1);
                this.setHp(this.getHp() + heals[i]);
                if (this.getHp() > this.MAXHP){
                    this.setHp(this.MAXHP);
                }
                return potion;
            }
        }
        return null;
    }

    public void autoHealInFight(){
        if (this.isLowHealth()){
            if (this.hasAnyPotion()){
                String used = useBestPotion();
                System.out.println("You used a " + Color.RED.get() + used + Color.RESET.get() + ". HP is now " + Color.BLUE.get() + (String.valueOf(this.getHp())) + Color.RESET.get() + "/" + Color.GREEN.get() + (String.valueOf(this.MAXHP)) + Color.RESET.get() + ".");
            } else {
                System.out.println(Color.RED.get() + "No potions!" + Color.RESET.get());
            }
        }if (this.getHp() <= 0 && this.bag.getOrDefault("revive", 0) > 0){
            this.setHp(this.MAXHP / 2);
            this.bag.put("revive", this.bag.get("revive") - 1);
            System.out.println("You are not dead yet! " + Color.RED.get() + "used a revive" + Color.RESET.get() + ". HP is now " + Color.BLUE.get() + (String.valueOf(this.getHp())) + Color.RESET.get() + "/" + Color.GREEN.get() + (String.valueOf(this.MAXHP)) + Color.RESET.get() + ".");
        }
    }

    public void recoverAfterBattle(){ 
        if (this.getHp() < this.MAXHP){
            int heal = (int)(this.MAXHP * (this.recovery / 100.0));
            this.setHp(Math.min(this.getHp() + heal, this.MAXHP));
            if(heal == 0){
                System.out.println("Current health: " + this.getHp() + "/" + this.MAXHP + ".");
            }else{
            System.out.println("You recovered " + heal + " HP after battle. HP is now " + this.getHp() + "/" + this.MAXHP + ".");
            }
        }
    }
    // ============= LVL CHECKING ============ 
    public void checkLvlUp(){ // checkes lvl up each time killing an enemy
        while (this.getXp() >= xpNeeded()){
            this.setLvl(this.getLvl() + 1);
            System.out.println(Color.GREEN.get() + "lvl Up! you are lvl " + String.valueOf(this.getLvl()) + Color.RESET.get());
            this.MAXHP += rand.nextInt(10) + 1 + this.getLvl();
            this.setHp(this.MAXHP);
            System.out.println("Your max hp is now " + this.MAXHP);
            this.modifier = (this.getLvl() / 2);
            System.out.println("Your ac is " + this.getAc());
            offerFeatSelection();
        }
    }

    // ================= FEATS METHODS ================

    public void chooseFeatureFromInput(Scanner scan){
        offerFeatSelectionWithInput(scan);
    }

    private List<Feat> createWeightedFeatList() {
        List<Feat> weightedList = new ArrayList<>();
        
        // Get all available feats
        Feat[] allFeats = Feat.values();
        
        // For each feat, add it to the list multiple times based on rarity
        for (Feat feat : allFeats) {
            int rarity = feat.rarity;
            
            // Weight calculation: lower rarity = more entries
            // Rarity 1 (common) gets 5 entries
            // Rarity 2 (uncommon) gets 4 entries
            // Rarity 3 (rare) gets 3 entries
            // Rarity 4 (epic) gets 2 entries
            // Rarity 5 (legendary) gets 1 entry
            int weight = 6 - rarity;
            
            // Add this feat 'weight' times to the weighted list
            for (int i = 0; i < weight; i++) {
                weightedList.add(feat);
            }
        }
        
        return weightedList;
    }

    private void offerFeatSelection(){
        // Randomly offer 2 out of 4 feats according to rarity
        List<Feat> weightedFeats = createWeightedFeatList();
        Feat feat1 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        Feat feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        while(feat2 == feat1){
            feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        }
        
        System.out.println(Color.PURPLE.get() + "\n===== FEAT SELECTION =====" + Color.RESET.get());
        System.out.println("You can choose one feat:");
        System.out.println("Option 1: ");
        feat1.display();
        System.out.println("\nOption 2: \n");
        feat2.display();
        storePendingFeats(feat1, feat2);
    }

    private Feat[] pendingFeat = new Feat[2];

    private void storePendingFeats(Feat f1, Feat f2){
        pendingFeat[0] = f1;
        pendingFeat[1] = f2;
    }

    public void offerFeatSelectionWithInput(Scanner scan){
        if(pendingFeat[0] == null) return;
        
        System.out.println("\nWhich feat do you choose? (1 or 2)");
        String choice = scan.next();
        
        if(choice.equals("1")){
            applyFeat(pendingFeat[0]);
        } else if(choice.equals("2")){
            applyFeat(pendingFeat[1]);
        } else {
            System.out.println("Invalid choice! Choosing feat 1...");
            applyFeat(pendingFeat[0]);
        }
        pendingFeat[0] = null;
        pendingFeat[1] = null;
    }

    public void offerBossFeat(){
        // Boss drops guarantee feat offer
        List<Feat> weightedFeats = createWeightedFeatList();
        Feat feat1 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        Feat feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        while(feat2 == feat1){
            feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        }
        Feat feat3 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        while(feat3 == feat1 || feat3 == feat2){
            feat3 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        }
        
        System.out.println(Color.CYAN.get() + "\n===== BOSS DEFEATED! FEAT REWARD =====" + Color.RESET.get());
        System.out.println("You defeated a boss! Choose one feat:");
        System.out.println("Option 1: ");
        feat1.display();
        System.out.println("\nOption 2: ");
        feat2.display();
        System.out.println("\nOption 3: ");
        feat3.display();
        storeBossFeat(feat1, feat2, feat3);
    }

    private Feat[] bossPendingFeat = new Feat[3];

    private void storeBossFeat(Feat f1, Feat f2, Feat f3){
        bossPendingFeat[0] = f1;
        bossPendingFeat[1] = f2;
        bossPendingFeat[2] = f3;
    }

    public void chooseBossFeat(Scanner scan){
        if(bossPendingFeat[0] == null) return;
        
        System.out.println("\nWhich feat do you choose? (1, 2, or 3)");
        String choice = scan.next();
        
        Feat chosen = null;
        if(choice.equals("1")){
            chosen = bossPendingFeat[0];
        } else if(choice.equals("2")){
            chosen = bossPendingFeat[1];
        } else if(choice.equals("3")){
            chosen = bossPendingFeat[2];
        } else {
            System.out.println("Invalid choice! Choosing feat 1...");
            chosen = bossPendingFeat[0];
        }
        applyFeat(chosen);
        bossPendingFeat[0] = null;
        bossPendingFeat[1] = null;
        bossPendingFeat[2] = null;
    }

    private void applyFeat(Feat feat){
        feat.apply(this);
        this.feats.put(feat, this.feats.getOrDefault(feat, 0) + 1);
    }

    public void displayFeats(){
        if(feats.isEmpty()){
            System.out.println("No feats acquired yet.");
            return;
        }
        System.out.println(Color.PURPLE.get() + "Your Feats:" + Color.RESET.get());
        for(Feat f : feats.keySet()){
            System.out.println("  - " + f.name + " (x" + feats.get(f) + ")");
        }
    }
    
    
    // =============== FINAL BOSS ===============
    
    public Enemy finalBossFight(Enemy enemy){
        int enemyXp = ((4 + this.getLvl()) + 19) * 2; //max final boss xp
        if(this.getLvl() == 9 && this.xpNeeded() <= enemyXp){
            enemy.finalBoss();
        }
        return enemy;
    }


}


//=========== ITEM CLASS ==========

class Item{ // potions, nothing else
    private String name;
    private int price;
    private int heal;
    private Random rand = new Random();
    
    public void makePotion(){ //for shops and enemies 
        int chance = rand.nextInt(20) + 1;
        if(chance <= 4){
            this.name = "weak potion";
            this.price = 10;
            this.heal = 4;
        }
        else if(chance >= 5 && chance <=12){
            this.name = "medium potion";
            this.price = 25;
            this.heal = 10;
        }
        else if(chance > 12 && chance <= 18){
            this.name = "strong potion";
            this.price = 40;
            this.heal = 15;
        }
        else if(chance > 18 && chance <= 19){
            this.name = "super potion";
            this.price = 50;
            this.heal = 20;
        }else if(chance == 20){
            this.name = "revive"; //not a potion but revives you to half HP!
            this.price = 120;
            this.heal = 1;
        }
    }
    
    // ===== GETTERS =====
    public String getName(){
        return this.name;
    }
    
    public int getPrice(){
        return this.price;
    }
    
    public int getHeal(){
        return this.heal;
    }

}

//============= ARMOR CLASS =============

class Armor{ // same logic as wep except for protaction
    private String name;
    private int ac;
    private int price;
    private Random rand = new Random();

    public void updateArmor(int playerlvl, Player player){
    int chance = rand.nextInt(6) + 1;
    if (chance == 1){
        this.name = "leather";
        this.ac = 1;
        this.price = 15;
    }else if (chance == 2){
        this.name = "chainmail";
        this.ac = 2;
        this.price = 30;
    }else if (chance == 3){
        this.name = "plate";
        this.ac = 3;
        this.price = 45;
    }else if (chance == 4){
        this.name = "mithril";
        this.ac = 4;
        this.price = 60;
    }else if (chance == 5){
        this.name = "adamantium";
        this.ac = 5;
        this.price = 75;
    }else if (chance == 6){
        this.name = "robe";
        this.ac = 0;
        this.price = 5;
    }__enhancerArm(playerlvl, player.getLuck());
    }

    // ===== GETTERS =====
    public String getName(){
        return this.name;
    }
    
    public int getAc(){
        return this.ac;
    }
    
    public int getPrice(){
        return this.price;
    }

    private void __enhancerArm(int playerlvl, int luck){ //add incentives for later games to use money other than potions
        int chance = rand.nextInt((100) + 1) + luck;
        if(playerlvl > 3){
            if(chance >= 50 && chance < 70){
                this.name += "+1";
                this.ac += 1;
                this.price += (int)(this.price * (0.30));
            }else if(chance >= 70 && chance < 80){
                this.name += "+2";
                this.ac += 2;
                this.price += (int)(this.price * (0.50));
            }else if (chance >= 80 && chance < 90){
                this.name = Color.GREEN.get() + "Epic " + Color.RESET.get() + this.name;
                this.ac += 3;
                this.price += (int)(this.price * (0.70));
            }else if(chance >= 90 && chance < 100 && playerlvl > 5){
                this.name = Color.PURPLE.get() + "Masterwork " + Color.RESET.get() + this.name;
                this.ac += 4;
                this.price += (int)(this.price * (0.90));
            }else if(chance == 100 && playerlvl > 6){
                this.name = Color.CYAN.get()  + "Legendary " + Color.RESET.get() + this.name;
                this.ac += 5;
                this.price += this.price;
            }
        }
        else if(playerlvl < 3){
            if(chance > 93){
                this.name = Color.GREEN.get() + "Epic " + Color.RESET.get() + this.name;
                this.ac += 3;
                this.price += (int)(this.price * (0.50));
            }else if(chance  > 87){
                this.name += "+2";
                this.ac += 2;
                this.price += (int)(this.price * (0.50));
            } else if (chance > 80){
                this.name += "+1";
                this.ac += 1;
                this.price += (int)(this.price * (0.30));
            }
        }
    }
}

//============== SHOP CLASS ==============

class Shop{ // Can be accesed at the end of fights
    private Item item = new Item();
    private Weapon wep = new Weapon();
    private Armor arm = new Armor();
    
    // ===== GETTERS =====
    public Item getItem(){
        return this.item;
    }
    
    public Weapon getWeapon(){
        return this.wep;
    }
    
    public Armor getArmor(){
        return this.arm;
    }


    public int canBuyWeapon(int gold, Weapon wepName, Scanner scan, Player user){ // check if user can buy wep
        System.out.println("There is " + wepName.getName() + " its price is " + wepName.getPrice());
        System.out.println("You have " + gold);
        if(gold >= wepName.getPrice()){
            System.out.println("Do you want to buy this weapon? " + wepName.getName() + " y/n");
            System.out.println("Damage die: " + wepName.getDiceCount() + "d" + wepName.getDiceType() + "+" + " " + wepName.getBonus());
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                gold -= wepName.getPrice();
                System.out.println("You bought " + wepName.getName());
                user.setAttack(0);
                user.setWeapon(wepName);
                user.setAttack(user.dmg());
            }else{
                System.out.println("You didnt buy it!");
            }
        }else{
            System.out.println("Insufficiant gold");
        }
        return gold;
    }

    public int canBuyItem(int gold, Item potion, Scanner scan, Player user){ // same as before but for item
        System.out.println("There is " + potion.getName() + " its price is " + potion.getPrice());
        System.out.println("You have " + gold);
        if(gold >= potion.getPrice()){
            System.out.println("Do you want to buy this potion? " + potion.getName() + " y/n");
            System.out.println("Heal for: " + potion.getHeal());
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                gold -= potion.getPrice();
                System.out.println("You bought " + potion.getName());
                user.getBag().put(potion.getName(), user.getBag().getOrDefault(potion.getName(), 0) + 1);
            }else{
                System.out.println("You didnt buy it!");
            }
        }else{
            System.out.println("Insufficiant gold");
        }
        return gold;
    }    

    public int canBuyArmor(int gold, Armor armName, Scanner scan, Player user){ // same as before but for armor
        if (this.arm.getAc() == 0){
            this.arm = new Armor();
        }
        System.out.println("There is " + armName.getName() + " its price is " + Color.YELLOW.get() + (String.valueOf(armName.getPrice())) + Color.RESET.get());
        System.out.println("You have " + Color.YELLOW.get() + (String.valueOf(gold) + Color.RESET.get()));
        if(gold >= armName.getPrice()){
            System.out.println("Do you want to buy this armor? " + armName.getName() + " y/n");
            System.out.println("Armor class: " + armName.getAc());
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                gold -= armName.getPrice();
                System.out.println("You bought " + armName.getName());
                user.setAc(user.getAc() - user.getArmor().getAc()); 
                user.setArmor(armName);
                user.setAc(user.getAc() + armName.getAc());
            }else{
                System.out.println("You didnt buy it!");
            }
        }
        return gold;
    }
    
}


//================FIGHT CLASS================


public class Fight{
    //=============== POISON METHODS ==================
    private Random rand = new Random(); // random number generator ie rolls 
    public static void tryApplyPoison(Player player, Enemy enemy){
        Random rand = new Random();
        int d100 = rand.nextInt(100) + 1;
        if((player.canCastPoison() == true && (d100 + player.getLuck()) > 80)){
            enemy.setPoisoned(true);
            enemy.setPoisonCount(3); //last 3 ticks
            System.out.println(enemy.getName() + Color.BGREEN.get() + " Is poisoned!" + Color.RESET.get());
        }
    }

    public static void processPoison(Player player, Enemy enemy){
        
        if(!enemy.isPoisoned()){
            return;
        }

        int hit = player.poisondmg();
        enemy.setHp(enemy.getHp() - hit);
        enemy.setPoisonCount(enemy.getPoisonCount() - 1);
        System.out.println(enemy.getName() + Color.BGREEN.get() + " Is poisoned! and was hit by: " + hit + Color.RESET.get() + " hp is : " + enemy.getHp());
        if(enemy.getPoisonCount() <= 0){
            enemy.setPoisoned(false);
            System.out.println(enemy.getName() + Color.BGREEN.get() + " Is not poisoned anymore!" + Color.RESET.get());
        }
    }


    //METHOD FOR ATTACKING
    public static int attack(int roll, Entity att, Entity def){ // takes in the "dice" roll, attacker attack, attacked ac, attacked hp, name of the attacker, name of the attacked and returning new attacked hp
        int crit = 20 - (att.getLuck() / 10);
        if (((roll + att.hitBonus()) >= def.getAc() && def.getHp() > 0 && roll != 1) || roll >= (crit)){
            if(roll >= crit){
                 //Critical double damgae
                int dmg = att.dmg() * 2;
                def.setHp(def.getHp() - dmg);
                System.out.println(Color.RED.get() + "CRITICAL!!! " + Color.RESET.get() + att.getName() + " hit! " + def.getName() + " for: " + Color.RED.get() + dmg + Color.RESET.get() + " hp is now at: " + def.getHp() + " points!");
            }else{
                int hit = att.dmg();
                def.setHp(def.getHp() - hit);
                System.out.println(att.getName() + " hit " + def.getName() + " for: " + Color.RED.get() + hit + Color.RESET.get() + " " + def.getName() + " hp is: " + def.getHp() + " points!");

            }
            if(att instanceof Player && def instanceof Enemy){ //poison machanics!
                Player p = (Player)att;
                Enemy e = (Enemy)def;
                tryApplyPoison(p, e);
            }
        }
        else if(roll == 1){ //1 is a critical miss no matter what
            System.out.println("CRITICAL MISS FOR " + att.getName());
        }
        else{
            System.out.println(att.getName() + " missed.");
        }
        return def.getHp();
    }

    // ============= Time Wait Method ===========

    public static void wait(int time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException a){
            System.out.println("Thread was interupted");
        }
    }
    
    // ============= Main ======================

    public static void main(String[] args){
        Random rand = new Random();
        Scanner scan = new Scanner(System.in); // for user inputs
        Player player = new Player(); // user player
        String userAnsware1 = "PLACE HOLDER"; // for if the user wants to play
        final String DEF_NAME = "hero";
        
        //diffrent types of counts 
        int fightCount = 0;
        int slimeCount = 0;
        int wolfCount = 0;
        int goblinCount = 0;
        int dragonCount = 0;
        int ratCount = 0;

        //the system will creat the player
        System.out.println("please enter your name");
        String userName = scan.nextLine();
        if( userName.equals("")){
            userName = DEF_NAME;
        }
        player.newPlayer(userName);

        System.out.println("Hello " + Color.ORANGE.get() + userName + Color.RESET.get() + ".");
        System.out.println("This are your stats: ");
        
        wait(500);
        
        System.out.println("hp: " + Color.BLUE.get() + player.getHp() + Color.RESET.get());
        System.out.println("ac: " + Color.RED.get() + player.getAc() + Color.RESET.get());
        System.out.println("Weapon: " + player.getWeapon().getName() + " damage die: " + player.getWeapon().getDiceCount() + "d" + player.getWeapon().getDiceType());
        System.out.println("Armor: " + player.getArmor().getName() + " ac: " + player.getArmor().getAc());
        System.out.println("Gold: " + Color.YELLOW.get() + player.getGold() + Color.RESET.get());
        wait(500);

        while(!userAnsware1.equals("y") && !userAnsware1.equals("yes")){ //loop for the first init
        System.out.println("Are you ready to start? y/n");
        userAnsware1 = scan.next();

        if(userAnsware1.equals("n") || userAnsware1.equals("no")){ 
            System.out.println("Goodbye");
            System.exit(0);
        }
        else if(userAnsware1.equals("y") || userAnsware1.equals("yes")){
            userAnsware1 = "yes";
        }
        }
        
        
            //========FIGHT BLOCK=======
        while (player.getHp() > 0 && (player.getLvl() < 10 || player.getBag().getOrDefault("revive", 0) > 0)){
            Enemy e = new Enemy(); //spawn enemy
            e.lvlBased(player.getLvl());
            if(player.getLvl() >= 3){
                e.isBoss(false);
            }

            if(player.getLvl() == 9){ // checking for final boss
                player.finalBossFight(e);
            }


            wait(500);

            System.out.println(player.getName() + " has met with " + e.getName() + "!" );
            System.out.println(player.getName() + " HP: " + player.getHp() + " AC: " + player.getAc() + " Level: " + player.getLvl());
            System.out.println(e.getName() + " HP: " + e.getHp() + " AC: " + e.getAc() + " Level: " + e.getLvl());
            
            wait(500);

            System.out.println("---- FIGHT ----");
            int count = 0; // For counting rounds
            while ((player.getHp() > 0 && e.getHp() > 0)){
            System.out.println("---- Round " + (count + 1) + "----");
            int rollE = rand.nextInt(20) + 1;
            int rollU = rand.nextInt(20) + 1;

            wait(1000);
  

            System.out.println("You rolled! " + (rollU));
            System.out.println("They rolled! " + (rollE));
            
                //=========USER HEALING========
            player.autoHealInFight();
            

                //=======ENEMY METHOD=======
            if(e.getHp() > 0){
                player.setHp(attack(rollE, e, player));
            }

                //=======USER METHOD=========
        
            if(player.getHp() > 0){
                e.setHp(attack(rollU, player, e));
            }
            processPoison(player, e);
                //========== END OF ROUND ==========
            count += 1;
            
            wait(500);

            System.out.println("------ End of round " + count + "------");
            }
            
            if(player.getHp() <= 0 && player.getBag().getOrDefault("revive", 0) <= 0){
                System.out.println("You have been defeated!");
        
                wait(1000);

                System.out.println("========= "+ Color.RED.get() + "R I P" + Color.RESET.get() + " =========");
                wait(300);
                System.out.println("Name: " + player.getName());
                System.out.println("lvl: " + Color.BLUE.get() + player.getLvl() + Color.RESET.get());
                System.out.println("hp: " + Color.GREEN.get() + player.getMaxHP() + Color.RESET.get());
                System.out.println("ac: " + player.getAc());
                System.out.println("gold: " + Color.YELLOW.get() + (String.valueOf(player.getGold())) + Color.RESET.get());
                System.out.println("You have fought: " + fightCount + " fights");
                System.out.println("was slayed by " + Color.RED.get() + e.getName() + Color.RESET.get());
                wait(300);
                System.out.println("=========================");

                wait(1000);

                System.exit(0);
            }else if(e.getHp() <= 0){
                if(e.getData().equals("Slime")){
                    slimeCount += 1;
                }else if(e.getData().equals("Wolf")){
                    wolfCount += 1;
                }else if(e.getData().equals("Goblin")){
                    goblinCount += 1;
                }else if(e.getData().equals("Dragon")){
                    dragonCount += 1;
                }else if(e.getData().equals("Rat")){
                    ratCount += 1;
                }
                System.out.println("You have defeated the enemy! \n");
                player.setXp(player.getXp() + e.getXp());
                player.setGold(player.getGold() + e.getGold());
                fightCount += 1;
                e.dropChance(player.getHp(), player.getLvl(), player.getBag(), player);;
                player.recoverAfterBattle();
                System.out.println("\nYou Got " + e.getXp() + " xp and " + Color.YELLOW.get() + (String.valueOf(e.getGold())) + Color.RESET.get() + " gold!\n");
                player.checkLvlUp();
                
                // Check if boss was defeated and offer feat
                if(e.isBossEnemy() == true){
                    player.offerBossFeat();
                }
                
                System.out.println("You are lvl: " + player.getLvl() + ". " + (player.xpNeeded() - player.getXp()) + " needed more xp to lvl up.  with a " + player.getWeapon().getName() + " that does: " + (player.getWeapon().getDiceCount() + player.getDiceCount()) + "d" + player.getWeapon().getDiceType() + " + " + (player.getWeapon().getBonus() + player.getModifier()) + " " + player.dicePoolToString() + " damage." );
                System.out.println("You also have: " + Color.GREEN.get() + player.getBag() + Color.RESET.get() + "\n");
                System.out.println("Armor: " + player.getArmor().getName() + " that gives: " + player.getArmor().getAc() + " ac.\n");
                System.out.println("You have: " + Color.YELLOW.get() + (String.valueOf(player.getGold())) + Color.RESET.get() + " gold.\n");
                player.displayFeats();
                System.out.println();
                
                Shop shop = new Shop();
                shop.getItem().makePotion();
                shop.getWeapon().update(rand.nextInt(player.getLvl() + 5) + 1, player.getLvl(), player);
                shop.getArmor().updateArmor(player.getLvl(), player);
                System.out.println("Please enter any key and press enter to continue.");
                
                scan.next();
                
                // Handle pending feat selections if any
                player.chooseFeatureFromInput(scan);
                if(e.isBossEnemy()){
                    player.chooseBossFeat(scan);
                }
                
                System.out.println("In the shop there are:");
                System.out.println(shop.getItem().getName() + " priced at: " + Color.YELLOW.get() + (String.valueOf(shop.getItem().getPrice())) + Color.RESET.get());
                System.out.println(shop.getWeapon().getName() + " priced at: " + Color.YELLOW.get() + (String.valueOf(shop.getWeapon().getPrice())) + Color.RESET.get());
                System.out.println(shop.getArmor().getName() + " priced at: " + Color.YELLOW.get() + (String.valueOf(shop.getArmor().getPrice())) + Color.RESET.get());
                System.out.println("Do you want to go to the shop? y/n");
                String shopInput = scan.next();
                if(shopInput.equals("y") || shopInput.equals("yes")){
                    player.setGold(shop.canBuyItem(player.getGold(), shop.getItem(), scan, player));
                    player.setGold(shop.canBuyWeapon(player.getGold(), shop.getWeapon(), scan, player));
                    player.setGold(shop.canBuyArmor(player.getGold(), shop.getArmor(), scan, player));
            }
        }
    }
    int score = (player.getXp() + ratCount + slimeCount + (wolfCount * 2) + (goblinCount * 3) + (dragonCount * 5) + player.getFeats().size()) / 7;
    System.out.println("======= " + Color.GREEN.get() + "W I N " + Color.RESET.get() + "=======");
    System.out.println("You have won the game!");
    System.out.println(player.getName() + " have fought: " + fightCount + " fights");
    System.out.println(player.getName() + " have: " + player.getGold() + " gold");
    System.out.println(player.getName() +  " You have: " + player.getXp() + " xp");
    System.out.println("Items: " + player.getArmor().getName() + ", " + player.getWeapon().getName() + ".");
    System.out.println("You have defeated: ");
    System.out.println(Color.YELLOW.get() + "Rats: " + ratCount + Color.RESET.get());
    System.out.println(Color.BLUE.get() + "Slimes: " + slimeCount + Color.RESET.get());
    System.out.println(Color.RED.get() + "Wolves: " + wolfCount + Color.RESET.get());
    System.out.println(Color.BGREEN.get() + "Goblins: " + goblinCount + Color.RESET.get());
    System.out.println(Color.PURPLE.get() + "Dragons: " + dragonCount + Color.RESET.get());
    System.out.println("\n" + Color.CYAN.get() + "Feats Acquired:" + Color.RESET.get());
    player.displayFeats();
    System.out.println("\nYour score is: " + score);
    System.out.println("=====================");
}
}
