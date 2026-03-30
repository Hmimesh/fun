import java.util.Scanner;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


// In this program we are doing a random to check who will win in a psudo very simplified dnd style combat
// @Ben Farjun
// @hmimesh @git.hub 


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

//=================== FEAT ENUM =====================

enum Feat{
    POWER("POWER", "Bonus damage +1", "damage", 1){
        public void apply(Player p){
            p.modifier += 1;
            System.out.println(Color.PURPLE.get() + "You gained POWER! Damage bonus +1" + Color.RESET.get());
        }
    },
    TANK("TANK", "Max HP +10", "health",  1){
        public void apply(Player p){
            p.MAXHP += 10;
            p.hp += 10;
            System.out.println(Color.CYAN.get() + "You gained TANK! Max HP +10" + Color.RESET.get());
        }
    },
    LUCK("LUCK", "Chance +10%", "luck", 3){
        public void apply(Player p){
            p.luck += 10;
            System.out.println(Color.YELLOW.get() + "You gained LUCK! Chance +10%" + Color.RESET.get());
        }
    },
    DODGE("DODGE", "AC +1 (Armor Class)", "defense", 1){
        public void apply(Player p){
            p.ac += 1;
            System.out.println(Color.GREEN.get() + "You gained DODGE! AC +1" + Color.RESET.get());
        }
    },
    DICER("DICER", "One more for attack dice", "damage",  5){
        public void apply(Player p){
            p.diceCount += 1;
            System.out.println(Color.ORANGE.get() + "You gained DICER! One more for attack dice" + Color.RESET.get());
        }
    },
    RICH("RICH", "Get 300 gold", "gold", 1){
        public void apply(Player p){
            p.gold += 300;
            System.out.println(Color.BGREEN.get() + "You gained RICH! 300 gold" + Color.RESET.get());
        }
    },
    ALCHEMIST("ALCHEMIST", "Better potions", "health", 2){
        public void apply(Player p){
            p.potionHeal += p.MAXHP / 10;
            System.out.println(Color.BGREEN.get() + "You gained ALCHEMIST! Better potions" + Color.RESET.get());
        }
    },
    FIREMAGIC("FIRE MAGIC", "Add 1d6 of fire damage", "damage", 5){
        public void apply(Player p){
            p.fireMage = true;
            p.dicePool();
            p.fireMage = false;
            System.out.println(Color.BRED.get() + "You gained FIRE MAGIC! Add 1d6 of fire damage" + Color.RESET.get());
        }
    },
    POISON("POISON", "Add a chance of 1d4 of poison damage", "damage", 3){
        public void apply(Player p){
            p.poisonMage = true;
            p.canPoison = true;
            p.dicePool();
            p.poisonMage = false;
            System.out.println(Color.BGREEN.get() + "You gained POISON! Add a consistent 1d4 of poison damage" + Color.RESET.get());
        }
    },
    WATERMAGE("Water Mage", "Add 1d8 of water damage", "damage", 5){
        public void apply(Player p){
            p.waterMage = true;
            p.dicePool();
            p.waterMage = false;
            System.out.println(Color.BLUE.get() + "You gained WATER MAGE! Add 1d8 of water damage" + Color.RESET.get());
        }
    },
    RECOVERY("Recovery", "heal 10% of max hp and the end of battles", "health", 2){
        public void apply(Player p){
            p.recovery += 10;
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


// ================= ENTETY CLASS =================
abstract class Entity{
    String name;
    int hp;
    int ac;
    int bonus;
    int lvl;
    int xp;
    int gold;
    int luck; 

    abstract int dmg();

    int hitBonus(){
       return bonus;
    }

}




//=================== ENEMY CLASS ====================

class Enemy extends Entity{ //the enemy class should be with hp for hit points, ac = for armor class, and attack for bonusus
    Random rand = new Random();
    String data;
    int attack;
    int diceCount;
    int diceType;
    int potionHeal;
    int poisonCount;
    boolean boss;
    boolean poisoned = false;

    public boolean isBoss(boolean ending){
        int chance =  rand.nextInt(100);
        boolean isFinal = ending;
        if(chance <= 20 || isFinal == true ){
            this.boss = true;
            this.name += Color.BRED.get() + " Boss" + Color.RESET.get();
            this.gold *= 2;
            this.xp *= 2;
            this.poisonCount = 0;
            this.ac += 2;
            this.diceCount += 1;
            this.luck += 10;
            this.hp += 5 * ((this.lvl / 2) + 1) ;
            this.bonus += rand.nextInt(5) + 1;
            if((rand.nextInt((100) + 1) - (this.lvl * 5) < 10 ) && this.lvl > 6){
                this.name = Color.CYAN.get() + "Legendary " + Color.RESET.get() + this.name;
                this.bonus += 1;
                this.ac += 1;
                this.hp += 10;
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
        damage += this.bonus;
        return damage;
    }

    public void lvlBased(int lvl){ //lvl is user lvl
        
        this.hp = rand.nextInt(lvl * 20 - lvl * 5 + 1) + lvl * 5;
        this.lvl = rand.nextInt(4) + lvl;
        int minXp = lvl;

        int maxBonus = Math.max(1, this.lvl / 2);
        
        if(this.hp <= lvl * 8 && this.hp >= lvl * 5){
            this.ac = 4 + lvl;
            this.diceType = 4;
            this.diceCount = 1 + ((lvl / 4));
            this.bonus = lvl / 3;
            this.poisonCount = 0;
            this.attack = dmg();
            this.xp = this.lvl / 4 + rand.nextInt(5) + 1;
            this.gold =  rand.nextInt(10) + this.xp;
            this.name = Color.BLUE.get() + "Slime" + Color.RESET.get();
            this.data = "Slime";
        }
        else if(this.hp > lvl * 8 && this.hp <= 12 * lvl){
            this.ac = rand.nextInt((7 + lvl) - (4 + lvl) + 1) + (4 + lvl);
            this.diceType = 6;
            this.diceCount = 1 + ((lvl / 4));
            this.bonus = rand.nextInt((maxBonus)) + 1;
            this.poisonCount = 0;
            this.attack = dmg();
            this.xp = this.lvl / 3 + rand.nextInt(6) + 2;
            this.gold = rand.nextInt(20) + this.xp;
            this.name = Color.RED.get() + "Wolf" + Color.RESET.get();
            this.data = "Wolf";
        }
        else if(this.hp > 12 * lvl && this.hp <= 17 * lvl){
            this.ac = rand.nextInt((8 + lvl) - (4 + lvl) + 1) + (4 + lvl);
            this.diceType = 8;
            this.diceCount = 1 + ((lvl / 4));
            this.bonus = rand.nextInt(maxBonus) + 3;
            this.poisonCount = 0;
            this.attack = dmg();
            this.xp = this.lvl / 2 + rand.nextInt(7) + 3;
            this.gold = rand.nextInt(40) + 1 + this.xp;
            this.name = Color.BGREEN.get() + "Goblin" + Color.RESET.get();
            this.data = "Goblin";
        }
        else if(this.hp > 17 * lvl && this.hp <= 20 * lvl && lvl >= 2){
            this.ac = rand.nextInt((10 + lvl) - (6 + lvl) + 1) + (6 + lvl);
            this.diceType = 6;
            this.diceCount = 2 + ((lvl / 4));
            this.bonus = lvl / 2 + 5;
            this.poisonCount = 0;
            this.attack = dmg();
            this.xp = this.lvl + rand.nextInt(15) +  4;
            this.gold = rand.nextInt(100) + 50;
            this.name = Color.PURPLE.get() + "Dragon" + Color.RESET.get();
            this.data = "Dragon";
        }else{
            this.hp = 10;
            this.ac = 2 + lvl;
            this.diceType = 4;
            this.diceCount = 1;
            this.poisonCount = 0;
            this.bonus = 1;
            this.attack = dmg();
            this.xp = 1;
            this.gold = 1;
            this.name = Color.YELLOW.get() + "RAT" + Color.RESET.get();
            this.data = "Rat";
        }
        if (this.xp <= 0){
            this.xp = minXp;
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
            chance = ((Uhp * 10) / Ulvl);
        }

        //drop chance is 40% with out player luck modifier
        if (60 > (rand.nextInt(100) + chance + player.luck)){
            Item drop = new Item();
            drop.makePotion();
            bag.put(drop.name, bag.getOrDefault(drop.name, 0) + 1);
            System.out.println("You found a " + drop.name);
        }else if(this.boss == true){ //if boss alwats get between 1 - 2 rewards
            Item drop = new Item();
            drop.makePotion();
            bag.put(drop.name, bag.getOrDefault(drop.name, 0) + 1);
            System.out.println("You found a " + drop.name);
        }     
        
        
    }

    public void finalBoss(){
        this.ac = rand.nextInt(10) + 7;
        this.hp = 18 * 9;
        this.diceType = 8;
        this.diceCount = 2;
        this.bonus = 5;
        this.poisonCount = 0;
        this.attack = dmg();
        this.xp = 33; // max xp giving by the dragon + 1
        this.gold = 1000;
        this.name = Color.CYAN.get() + "TACHO" + Color.RESET.get();
        this.data = "Final Boss";
        this.isBoss(true);
        this.name = Color.BRED.get() + "Final " + Color.RESET.get() + this.name;
    }

    
}

//============== WEAPON CLASS ==================

class Weapon{ // weapons and their abilities
    String name;
    int diceCount; //number of dices
    int diceType; // type of dice
    int data; // ID for the damage stats
    int bonus;
    int price;


    public void update(int dmg, int playerlvl, Player player){  // will be called when buying or first opening the game
        this.data = dmg; 
        if(this.data <= 4){
            this.name = "Dagger";
            this.diceCount = 1;
            this.diceType = 4;
        }
        else if(this.data >= 5 && this.data <= 8){
            this.name = "Sword";
            this.diceCount = 1;
            this.diceType = 8;
        }
        else if(this.data > 8 && this.data <= 12){
            this.name = "Battle Axe";
            this.diceCount = 1;
            this.diceType = 12;
        }else{
            this.name = "Dragon slayer";
            this.diceCount = 2;
            this.diceType = 6;
        }
        this.price = this.data * 10;
        checkBonusDice(playerlvl, player.luck);
        enhancerWep(playerlvl, player.luck);
        

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

//========== PLAYER CLAS ============

class Player extends Entity{
    int MAXHP;
    int MAXAC;
    int modifier;
    Weapon wep;
    // =========== Booleans feats ==========
    boolean fireMage = false;
    boolean waterMage = false;
    boolean poisonMage = false;
    boolean canPoison = false;

    // ========== Booleans feats end =========
    int potionHeal;
    int diceCount;
    int recovery;
    int attack;
    Item item; // holder for potions for easy accsess for the bag
    Armor arm;

    Map<String, Integer> bag = new HashMap<>(); //bag for diff potions
    Map<Feat, Integer> feats = new HashMap<>(); //track acquired feats and count
    Map<Integer, Integer> dicepool = new HashMap<>();
    Map<Integer, Integer> poisonpool= new HashMap<>();


    Random rand = new Random();


    public void newPlayer(String newName){ //when starting new game init for the user
        this.name = Color.ORANGE.get() + newName + Color.RESET.get();
        this.lvl = 1;
        this.xp = 0;
        this.MAXHP = rand.nextInt(6) + 1 + 30;
        this.hp = this.MAXHP;
        this.luck = 0;
        this.arm = new Armor();
        this.diceCount = 0;
        this.arm.updateArmor(this.lvl, this);
        this.ac = 10 + this.arm.ac;
        this.wep = new Weapon();
        this.item = new Item();
        this.gold = rand.nextInt(100 - (this.arm.price) - this.wep.price) + 26;    
        item.makePotion();
        this.bag.put(this.item.name, bag.getOrDefault(this.item.name, 0) + 1);
        wep.update(rand.nextInt(4) + 1, this.lvl, this);
        this.attack = dmg();
    }

    public int xpNeeded(){ // for lvl ups
        int xpNeeded = (this.lvl + 1) * (this.lvl + 2) * 5;
        return xpNeeded;
    }
   // ========= DAMAGE METHODS ============ 
    public void dicePool(){
        if(fireMage == true){
            this.dicepool.put(6, 1);
        }else if(this.waterMage == true){
            this.dicepool.put(8, 1);
        }else if(this.poisonMage == true){
            this.poisonpool.put(4, 1);
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
                        System.out.println("Fire damage: " + Color.RED.get() + roll + Color.RESET.get());
                        break;
                    case 8:
                        System.out.println("Water damage: " + Color.BLUE.get() + roll + Color.RESET.get());
                        break;
                }
            }
        }
        return extraDamage;
    }

    @Override
    int hitBonus() {
        return this.modifier + this.wep.bonus;
    }


    public int dmg(){ // main damage algo
        int damage = 0;;
        for (int i = 0; i < wep.diceCount + this.diceCount; i++){
            damage += rand.nextInt(wep.diceType) + 1;
        }
        damage += rollExtraDice();
        damage += wep.bonus + this.modifier;
        return damage;
    }

    // =============== HEALTH METHODS ===============
    public boolean isLowHealth(){
        return this.hp > 0 && this.hp <= (int)(this.MAXHP * 0.45);
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
                this.hp += heals[i];
                if (this.hp > this.MAXHP){
                    this.hp = this.MAXHP;
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
                System.out.println("You used a " + Color.RED.get() + used + Color.RESET.get() + ". HP is now " + Color.BLUE.get() + (String.valueOf(this.hp)) + Color.RESET.get() + "/" + Color.GREEN.get() + (String.valueOf(this.MAXHP)) + Color.RESET.get() + ".");
            } else {
                System.out.println(Color.RED.get() + "No potions!" + Color.RESET.get());
            }
        }if (this.hp <= 0 && this.bag.getOrDefault("revive", 0) > 0){
            this.hp = this.MAXHP / 2;
            this.bag.put("revive", this.bag.get("revive") - 1);
            System.out.println("You are not dead yet! " + Color.RED.get() + "used a revive" + Color.RESET.get() + ". HP is now " + Color.BLUE.get() + (String.valueOf(this.hp)) + Color.RESET.get() + "/" + Color.GREEN.get() + (String.valueOf(this.MAXHP)) + Color.RESET.get() + ".");
        }
    }

    public void recoverAfterBattle(){ 
        if (this.hp < this.MAXHP){
            int heal = (int)(this.MAXHP * (this.recovery / 100.0));
            this.hp = Math.min(this.hp + heal, this.MAXHP);
            if(heal == 0){
                System.out.println("Current health: " + this.hp + "/" + this.MAXHP + ".");
            }else{
            System.out.println("You recovered " + heal + " HP after battle. HP is now " + this.hp + "/" + this.MAXHP + ".");
            }
        }
    }
    // ============= LVL CHECKING ============ 
    public void checkLvlUp(){ // checkes lvl up each time killing an enemy
        while (this.xp >= xpNeeded()){
            this.lvl += 1;
            System.out.println(Color.GREEN.get() + "lvl Up! you are lvl " + String.valueOf(this.lvl) + Color.RESET.get());
            this.MAXHP += rand.nextInt(10) + 1 + this.lvl;
            this.hp = this.MAXHP;
            System.out.println("Your max hp is now " + this.MAXHP);
            this.modifier = (this.lvl / 2);
            System.out.println("Your ac is " + this.ac);
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
        int enemyXp = ((4 + this.lvl) + 19) * 2; //max final boss xp
        if(this.lvl == 9 && this.xpNeeded() > enemyXp){
            enemy.finalBoss();
        }
        return enemy;
    }


}


//=========== ITEM CLASS ==========

class Item{ // potions, nothing else
    String name;
    int price;
    int heal;
    Random rand = new Random();
    
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

}

//============= ARMOR CLASS =============

class Armor{ // same logic as wep except for protaction
    String name;
    int ac;
    int price;
    Random rand = new Random();

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
    }__enhancerArm(playerlvl, player.luck);
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
    Item item = new Item();
    Weapon wep = new Weapon();
    Armor arm = new Armor();


    public int canBuyWeapon(int gold, Weapon wepName, Scanner scan, Player user){ // check if user can buy wep
        System.out.println("There is " + wepName.name + " its price is " + wepName.price);
        System.out.println("You have " + gold);
        if(gold >= wepName.price){
            System.out.println("Do you want to buy this weapon? " + wepName.name + " y/n");
            System.out.println("Damage die: " + wep.diceCount + "d" + wep.diceType + "+" + " " + wep.bonus);
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                gold -= wepName.price;
                System.out.println("You bought " + wepName.name);
                user.attack = 0;
                user.wep = wepName;
                user.attack = user.dmg();
            }else{
                System.out.println("You didnt buy it!");
            }
        }else{
            System.out.println("Insufficiant gold");
        }
        return gold;
    }

    public int canBuyItem(int gold, Item potion, Scanner scan, Player user){ // same as before but for item
        System.out.println("There is " + potion.name + " its price is " + potion.price);
        System.out.println("You have " + gold);
        if(gold >= potion.price){
            System.out.println("Do you want to buy this potion? " + potion.name + " y/n");
            System.out.println("Heal for: " + potion.heal);
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                gold -= potion.price;
                System.out.println("You bought " + potion.name);
                user.bag.put(potion.name, user.bag.getOrDefault(potion.name, 0) + 1);
            }else{
                System.out.println("You didnt buy it!");
            }
        }else{
            System.out.println("Insufficiant gold");
        }
        return gold;
    }    

    public int canBuyArmor(int gold, Armor armName, Scanner scan, Player user){ // same as before but for armor
        if (this.arm.ac == 0){
            this.arm.ac += 1;
        }
        System.out.println("There is " + armName.name + " its price is " + Color.YELLOW.get() + (String.valueOf(armName.price)) + Color.RESET.get());
        System.out.println("You have " + Color.YELLOW.get() + (String.valueOf(gold) + Color.RESET.get()));
        if(gold >= armName.price){
            System.out.println("Do you want to buy this armor? " + armName.name + " y/n");
            System.out.println("Armor class: " + armName.ac);
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                gold -= armName.price;
                System.out.println("You bought " + armName.name);
                user.ac -= user.arm.ac; 
                user.arm = armName;
                user.ac += armName.ac;
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
    Random rand = new Random(); // random number generator ie rolls 
    public  static void tryApplyPoison(Player player, Enemy enemy){
        Random rand = new Random();
        int d100 = rand.nextInt(100) + 1;
        if((player.canPoison == true && (d100 + player.luck) > 80)){
            enemy.poisoned = true;
            enemy.poisonCount = 3; //last 3 ticks
            System.out.println(enemy.name + Color.BGREEN.get() + " Is poisoned!" + Color.RESET.get());
        }
    }

    public static void processPoison(Player player, Enemy enemy){
        
        if(!enemy.poisoned){
            return;
        }

        int hit = player.poisondmg();
        enemy.hp -= hit;
        enemy.poisonCount--;
        System.out.println(enemy.name + Color.BGREEN.get() + " Is poisoned! and was hit by: " + hit + Color.RESET.get());
        if(enemy.poisonCount <= 0){
            enemy.poisoned = false;
            System.out.println(enemy.name + Color.BGREEN.get() + " Is not poisoned anymore!" + Color.RESET.get());
        }
    }


    //METHOD FOR ATTACKING
    public static int attack(int roll, Entity att, Entity def){ // takes in the "dice" roll, attacker attack, attacked ac, attacked hp, name of the attacker, name of the attacked and returning new attacked hp
        int crit = 20 - (att.luck / 10);
        if (((roll + att.hitBonus()) >= def.ac && def.hp > 0 && roll != 1) || roll >= (crit)){
            if(roll >= crit){
                 //Critical double damgae
                int dmg = att.dmg() * 2;
                def.hp -= dmg ;
                System.out.println(Color.RED.get() + "CRITICAL!!! " + Color.RESET.get() + att.name + " hit! " + def.name + " for: " + Color.RED.get() + dmg + Color.RESET.get() + " hp is now at: " + def.hp + " points!");
            }else{
                int hit = att.dmg();
                def.hp -= hit;
                System.out.println(att.name + " hit " + def.name + " for: " + Color.RED.get() + hit + Color.RESET.get() + " " + def.name + " hp is: " + def.hp + " points!");

            }
            if(att instanceof Player && def instanceof Enemy){ //poison machanics!
                Player p = (Player)att;
                Enemy e = (Enemy)def;
                tryApplyPoison(p, e);
            }
        }
        else if(roll == 1){ //1 is a critical miss no matter what
            System.out.println("CRITICAL MISS FOR " + att.name);
        }
        else{
            System.out.println(att.name + " missed.");
        }
        return def.hp;
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
            userName = "hero";
        }
        player.newPlayer(userName);

        System.out.println("Hello " + Color.ORANGE.get() + userName + Color.RESET.get() + ".");
        System.out.println("This are your stats: ");
        
        wait(500);
        
        System.out.println("hp: " + Color.BLUE.get() + player.hp + Color.RESET.get());
        System.out.println("ac: " + Color.RED.get() + player.ac + Color.RESET.get());
        System.out.println("Weapon: " + player.wep.name + " damage die: " + player.wep.diceCount + "d" + player.wep.diceType);
        System.out.println("Armor: " + player.arm.name + " ac: " + player.arm.ac);
        System.out.println("Gold: " + Color.YELLOW.get() + player.gold + Color.RESET.get());
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
        while (player.hp > 0 && player.lvl < 10){
            Enemy e = new Enemy(); //spawn enemy
            e.lvlBased(player.lvl);
            if(player.lvl >= 3){
                e.isBoss(false);
            }

            if(player.lvl == 9){ // checking for final boss
                player.finalBossFight(e);
            }


            wait(500);

            System.out.println(player.name + " has met with " + e.name + "!");
            System.out.println(player.name + " HP: " + player.hp + " AC: " + player.ac);
            System.out.println(e.name + " HP: " + e.hp + " AC: " + e.ac);
            
            wait(500);

            System.out.println("---- FIGHT ----");
            int count = 0; // For counting rounds
            while ((player.hp > 0 && e.hp > 0)){
            System.out.println("---- Round " + (count + 1) + "----");
            int rollE = rand.nextInt(20) + 1;
            int rollU = rand.nextInt(20) + 1;

            wait(1000);
  

            System.out.println("You rolled! " + (rollU));
            System.out.println("They rolled! " + (rollE));
            
                //=========USER HEALING========
            player.autoHealInFight();
            

                //=======ENEMY METHOD=======
            if(e.hp > 0){
                player.hp = attack(rollE, e, player);
            }

                //=======USER METHOD=========
        
            if(player.hp > 0){
                e.hp = attack(rollU, player, e);
            }
            processPoison(player, e);
                //========== END OF ROUND ==========
            count += 1;
            
            wait(500);

            System.out.println("------ End of round " + count + "------");
            }
            
            if(player.hp <= 0 && player.bag.getOrDefault("revive", 0) <= 0){
                System.out.println("You have been defeated!");
        
                wait(1000);

                System.out.println("========= "+ Color.RED.get() + "R I P" + Color.RESET.get() + " =========");
                wait(300);
                System.out.println("Name: " + player.name);
                System.out.println("lvl: " + Color.BLUE.get() + player.lvl + Color.RESET.get());
                System.out.println("hp: " + Color.GREEN.get() + player.MAXHP + Color.RESET.get());
                System.out.println("ac: " + player.ac);
                System.out.println("gold: " + Color.YELLOW.get() + (String.valueOf(player.gold)) + Color.RESET.get());
                System.out.println("You have fought: " + fightCount + " fights");
                System.out.println("was slayed by " + Color.RED.get() + e.name + Color.RESET.get());
                wait(300);
                System.out.println("=========================");

                wait(1000);

                System.exit(0);
            }else if(e.hp <= 0){
                if(e.data.equals("Slime")){
                    slimeCount += 1;
                }else if(e.data.equals("Wolf")){
                    wolfCount += 1;
                }else if(e.data.equals("Goblin")){
                    goblinCount += 1;
                }else if(e.data.equals("Dragon")){
                    dragonCount += 1;
                }else if(e.data.equals("Rat")){
                    ratCount += 1;
                }
                System.out.println("You have defeated the enemy! \n");
                player.xp += e.xp;
                player.gold += e.gold;
                fightCount += 1;
                e.dropChance(player.hp, player.lvl, player.bag, player);;
                player.recoverAfterBattle();
                System.out.println("\nYou Got " + e.xp + " xp and " + Color.YELLOW.get() + (String.valueOf(e.gold)) + Color.RESET.get() + " gold!\n");
                player.checkLvlUp();
                
                // Check if boss was defeated and offer feat
                if(e.boss == true){
                    player.offerBossFeat();
                }
                
                System.out.println("You are lvl: " + player.lvl + ". " + (player.xpNeeded() - player.xp) + " needed more xp to lvl up.  with a " + player.wep.name + " that does: " + (player.wep.diceCount + player.diceCount) + "d" + player.wep.diceType + " + " + (player.wep.bonus + player.modifier) + " damage." );
                System.out.println("You also have: " + Color.GREEN.get() + player.bag + Color.RESET.get() + "\n");
                System.out.println("Armor: " + player.arm.name + " that gives: " + player.arm.ac + " ac.\n");
                System.out.println("You have: " + Color.YELLOW.get() + (String.valueOf(player.gold)) + Color.RESET.get() + " gold.\n");
                player.displayFeats();
                System.out.println();
                
                Shop shop = new Shop();
                shop.item.makePotion();
                shop.wep.update(rand.nextInt(player.lvl + 5) + 1, player.lvl, player);
                shop.arm.updateArmor(player.lvl, player);
                System.out.println("Please enter any key and press enter to continue.");
                
                scan.next();
                
                // Handle pending feat selections if any
                player.chooseFeatureFromInput(scan);
                if(e.boss){
                    player.chooseBossFeat(scan);
                }
                
                System.out.println("In the shop there are:");
                System.out.println(shop.item.name + " priced at: " + Color.YELLOW.get() + (String.valueOf(shop.item.price)) + Color.RESET.get());
                System.out.println(shop.wep.name + " priced at: " + Color.YELLOW.get() + (String.valueOf(shop.wep.price)) + Color.RESET.get());
                System.out.println(shop.arm.name + " priced at: " + Color.YELLOW.get() + (String.valueOf(shop.arm.price)) + Color.RESET.get());
                System.out.println("Do you want to go to the shop? y/n");
                String shopInput = scan.next();
                if(shopInput.equals("y") || shopInput.equals("yes")){
                    player.gold = shop.canBuyItem(player.gold, shop.item, scan, player);
                    player.gold = shop.canBuyWeapon(player.gold, shop.wep, scan, player);
                    player.gold = shop.canBuyArmor(player.gold, shop.arm, scan, player);
            }
        }
    }
    int score = (player.xp + ratCount + slimeCount + (wolfCount * 2) + (goblinCount * 3) + (dragonCount * 5) + player.feats.size()) / 7;
    System.out.println("======= " + Color.GREEN.get() + "W I N " + Color.RESET.get() + "=======");
    System.out.println("You have won the game!");
    System.out.println(player.name + " have fought: " + fightCount + " fights");
    System.out.println(player.name + " have: " + player.gold + " gold");
    System.out.println(player.name +  " You have: " + player.xp + " xp");
    System.out.println("Items: " + player.arm.name + ", " + player.wep.name + ".");
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