package com.hmimesh.game;
import java.util.Scanner;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;


/** In this program we are doing a random to check who will win in a psudo very simplified dnd style combat
 * 
 * notice this is an onging and educational program from things i learn an implement
 * if you want to use it do with it something else you are welcome to!
 * 
 * check me out in github - Hmimesh
 * 
 * @author Ben Farjun
 * @version 01/04/2026
*/ 



//================== WINDOW CLASS ===================
class GameWindow{
    private boolean _visible;
    private String _name;
    private JFrame _frame;
    private JTextArea _logArea;
    private JTextArea _sceneArea;
    private JSplitPane _splitPane;
    private JTextField _inputField;
    private JScrollPane _logScroll;
    private JScrollPane _sceneScroll;
    private JPanel _bottomPanel; 
    private JButton _sendButton;
    private Player _player;
    private boolean waitingForName = true;
    private String _DEAFULT_NAME = "hero";

    public  GameWindow(String windowname, boolean visible){
        this._name = windowname;
        this._visible = visible;
        

        _frame = new JFrame(windowname);
        _logArea = new JTextArea();
        _sceneArea = new JTextArea();
        _inputField = new JTextField();
        _sendButton = new JButton("Send");
        _logScroll = new JScrollPane(_logArea);
        _sceneScroll = new JScrollPane(_sceneArea);
        _bottomPanel = new JPanel(new BorderLayout());
        _player = new Player ();

        // scene style no edits
        _sceneArea.setEditable(false);
        _sceneArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); //ASCII font
        
        // log style no edits  
        _logArea.setEditable(false);
        _logArea.setLineWrap(true);
        _logArea.setWrapStyleWord(true);

        _splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            _sceneScroll,
            _logScroll
        );

        _splitPane.setDividerLocation(400); //more for the scene that is on top less for the logger

        _bottomPanel.setLayout(new BorderLayout());
        _bottomPanel.add(_inputField, BorderLayout.CENTER);
        _bottomPanel.add(_sendButton, BorderLayout.EAST);

        _frame.setLayout(new BorderLayout());
        _frame.add(_bottomPanel, BorderLayout.SOUTH);
        _frame.add(_splitPane, BorderLayout.CENTER);
        _frame.add(_bottomPanel, BorderLayout.SOUTH);

        print("Welcom to the game!");
        print("Please enter your name");
        drawScene("""
                [_________ \\    ||        ||   ||       ||   _=_=_=_=       =========
                ||        ||    ||        ||   ||\\\\     ||   ||           |/         \\|
                ||        ||    ||        ||   || \\\\    ||   ||   |___    ||         ||
                ||        ||    ||        ||   ||  \\\\   ||   ||   |===\\\\  ||         ||  
                ||        ||    ||        ||   ||   \\\\  //   ||       ||  |\\         /|
                ||_______/_/    |_\\_______||   ||    \\\\//    ||\\______||   \\\\_______//
                
                 GOOD LUCK       HAVE FUN       A GAME MADE BY BEN FARJUN   @HMIMESH
                
                """);

        _sendButton.addActionListener(e ->{
            String text = _inputField.getText();
            if(!text.equals("")){
                print("> " + text);
                _inputField.setText("");
            }
        });

        _inputField.addActionListener(e -> {
            String text = _inputField.getText();
            if(!text.equals("")){
                print("> " + text);
                _inputField.setText("");
            }
        });
        
        _sendButton.addActionListener(e -> submitInput());
        _inputField.addActionListener(e -> submitInput());


        _sceneArea.setBackground(java.awt.Color.BLACK);
        _sceneArea.setForeground(java.awt.Color.GREEN);

        _logArea.setBackground(java.awt.Color.BLACK);
        _logArea.setForeground(java.awt.Color.WHITE);

        _inputField.setBackground(java.awt.Color.DARK_GRAY);
        _inputField.setForeground(java.awt.Color.WHITE);

        //set window
        _frame.setSize(600, 500);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setVisible(visible);
    }

    public void print(String text){
        _logArea.append(text + "\n");
    }

    public void drawScene(String scene){
        _sceneArea.setText(scene);
    }
        
    //=========== GETTERS ============
    public String getName(){
        return _name;
    }
    public boolean getVisible(){
        return _visible;
    }

    //============= SETTERS ============
    public void setName(String name){
        this._name = name;
        this._frame.setTitle(name);
    }

    public void setVisible(boolean visible){
        this._visible = visible;
        this._frame.setVisible(visible);
    }

    private void handleInput(String text){

        if(waitingForName){
            createNewPlayer(text);
            waitingForName = false;
            return;
        }

        Commands cmd = Commands.fromInput(text);

        if(cmd != null && _player != null){
            cmd.enable(this, _player);
            return;
        }

        print("Unknow command, type help");
    }

    private void submitInput(){
        String text = _inputField.getText().trim();
        if(text.equals("")){
            return;
        }

        print("> " + text);
        _inputField.setText("");
        handleInput(text);
    }

    private void createNewPlayer(String name){
        if(name.equals("")){
            name = _DEAFULT_NAME;
        }

        _player.newPlayer(name);
        print("Welcome " + _player.getName() + "! and get ready for adventure!");
    }

  
}
    
 
//================= COMMAND ENUM =====================

enum Commands{
    HELP("help", "HELP", "h", "Help"){
        public void enable(GameWindow game, Player player) {
            game.print("Commands: \n");
            game.print("Before fight when facing with doors:  \n");
            game.print("easy / e - for fighting easy creature \n");
            game.print("hard / h - for fighting hard creature \n");
            game.print("In shop and when asked: \n");
            game.print("yes / y - for aggreing \n");
            game.print("no / n - for disagreaing \n");
            game.print("Terms: \n");
            game.print("hp = health points you lose them you die. \n" );
            game.print("ac = Armor class how hard are you to get hit. \n");
            game.print("Dice pool = how many dice to hit you have \n");
            game.print("Dice type = which type of dice you or your weapon have \n");
            game.print("Weapon = your main way to attack \n");
            game.print("Gold = money to buy things can get from enemies \n");
            game.print("Xp = expirience points needed to level up \n");
            game.print("Level = how strong an entity is \n");
            game.print("Feats = features to get stronger in diffrent ways \n");
            game.print("Items = items to use in battles (potions) \n");
            game.print("Commands \n");
            game.print("inventory / i - opens inventory \n");
            game.print("feats / f - open your current feats \n");
            game.print("stats / s - open your current stats \n");
            
        }
    },
    FEATS("feat", "f", "Feats", "F"){
        public void enable(GameWindow game, Player player) {
            game.print("Feats : " + player.getFeats() + "\n");
        }
    },
    INVENTORY("inventory", "i", "Inventory", "I"){
        public void enable(GameWindow game, Player player){
            game.print(player.getName() +" inventory: \n");
            game.print("|                   |\n");
            game.print("| ARMOR: " + player.getArmor().getName() + "    |\n");
            game.print("| WEAPON: " + player.getWeapon() + "            |\n");
            game.print("| ITEMS: " + player.getBag() + "            |\n");
            game.print("| Gold: " + player.getGold() + "            |\n");
        }

    },
    STATS("stats", "s", "Stats", "S"){
        public void enable(GameWindow game, Player player){
            game.print(player.getName() + " here are your stats: \n");
            game.print("AC: " + player.getAc() + "\n");
            game.print("HP: " + player.getHp() + "/" + player.getMaxHP() + "\n");
            game.print("Dice pool: " + player.getDiceCount());
        }
    };

    String name1;
    String name2;
    String name3;
    String name4;

    Commands(String name1, String name2, String name3, String name4){
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.name4 = name4;
        List<String> nameTotal = new ArrayList<>();
        nameTotal.add(name1);
        nameTotal.add(name2);
        nameTotal.add(name3);
        nameTotal.add(name4);
    }

    public boolean matches(String input){
        return input.equalsIgnoreCase(name1)
        || input.equalsIgnoreCase(name2)
        || input.equalsIgnoreCase(name3)
        || input.equalsIgnoreCase(name4);
    }

    public static Commands fromInput(String input){
        for(Commands cmd : Commands.values()){
            if(cmd.matches(input)){
                return cmd;
            }
        }
        return null;
    }

    public abstract void enable(GameWindow game, Player player);


}




//=================== COLOR ENUM ===================== 
//TO DO CHANGE TO SWING COLOR SYSTEM

/**
 * Acolor configuration checks if the users have a terminal ie console
 * 
 */
class AcolorConfig{
    public static final boolean USE_COLOR = System.console() != null;
}
/**
 * enum for colors for terminal!
 * 
 */
enum Acolor{ // ANSI colors wrok only on more newer terminals, if wanna use it and see it please run on something like WSL / Powershell / Linux etc... and not on something like BlueJ
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    ORANGE("\u001B[38;5;208m"),
    BGREEN("\u001B[38;5;46m"),
    BRED("\u001B[38;5;196m"),
    PINK("\u001B[38;5;213m"),
    BBLUE("\u001B[38;5;39m"),
    BORANGE("\u001B[38;5;214m"),
    RESET("\u001B[0m");

    private final String code;
    /**
     * the strings for color or ending
     * 
     * @param code take the lines needed for said colors
     */
    Acolor(String code){
        this.code = code;
    }

    /**
     * a simpler way to return color or ending the color4
     * 
     * @return return color or reset
     */
    public String get(){
        return AcolorConfig.USE_COLOR ? code : "";
    }
}

// ================= ENTETY CLASS =================
/**
 * Entity class parant to the enemy and player class
 */
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

    /**
     * hit bonus for dice rolls and adding hits
     * @return the bonus modifier || bonus
     */
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
/**
 * Enemy class for battles.
 */
class Enemy extends Entity{ //the enemy class should be with hp for hit points, ac = for armor class, and attack for bonusus
    private Random rand = new Random();
    private String data;
    private int attack;
    private int diceCount;
    private int diceType;
    private int poisonCount;
    private boolean boss;
    private boolean poisoned = false;
    
    /**
    * isBoss check to see if the enemy is boss and return a boolean if he is boss
    * 
    * @param ending boolean check if ending
    * @return return a boolean if the enemy is a boss
    */

    public boolean isBoss(boolean ending){
        int chance =  rand.nextInt(100);
        boolean isFinal = ending;
        if(chance <= 20 || isFinal == true ){
            this.boss = true;
            this.setName(this.getName() + Acolor.BRED.get() + " Boss" + Acolor.RESET.get());
            this.setGold(this.getGold() * 2);
            this.setXp(this.getXp() * 2);
            this.poisonCount = 0;
            this.setAc(this.getAc() + 2);
            this.diceCount += 1;
            this.setLuck(this.getLuck() + 10);
            this.setHp(this.getHp() + 5 * ((this.getLvl() / 2) + 1));
            this.setBonus(this.getBonus() + rand.nextInt(5) + 1);
            if((rand.nextInt((100) + 1) - (this.getLvl() * 5) < 10 ) && this.getLvl() > 6){
                this.setName(Acolor.CYAN.get() + "Legendary " + Acolor.RESET.get() + this.getName());
                this.setBonus(this.getBonus() + 1);
                this.setAc(this.getAc() + 1);
                this.setHp(this.getHp() + 10);
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * dmg method for caclulating damage done by this entity
     * 
     * @return a damage value
     */

    public int dmg(){
        int damage = 0;
        Random rand = new Random();
        for (int i = 0; i < this.diceCount; i++){
            damage += rand.nextInt(this.diceType) + 1;
        }
        damage += this.getBonus();
        return damage;
    }

    /**
     * Configure this enemy based on player lvl
     * 
     * @param lvl the player level
     */

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
            this.setName(this.getName() + Acolor.GREEN.get() + " Lucky" + Acolor.RESET.get());
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
            this.setName(Acolor.BLUE.get() + "Slime" + Acolor.RESET.get());
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
            this.setName(Acolor.RED.get() + "Wolf" + Acolor.RESET.get());
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
            this.setName(Acolor.BGREEN.get() + "Goblin" + Acolor.RESET.get());
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
            this.setName(Acolor.PURPLE.get() + "Dragon" + Acolor.RESET.get());
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
            this.setName(Acolor.YELLOW.get() + "RAT" + Acolor.RESET.get());
            this.data = "Rat";
        }
        if (this.getXp() <= 0){
            this.setXp(minXp);
        }
    }

    /**
     * a check to see if the player get an item drop
     * 
     * @param Uhp User(aka player) health points
     * @param Ulvl User(aka Player) level
     * @param bag a map of bag with String and integer 
     * @param player a player class
     */
    public void dropChance(int Uhp, int Ulvl, Map<String, Integer> bag, Player player){ //item drops, potions will be called when enemy is dead
        int baseChance = 20;
        switch(this.data){
            case "Slime":
                baseChance += 5;
                break;
            case "Wolf":
                baseChance += 10;
                break;
            case "Goblin":
                baseChance += 15;
                break;
            case "Dragon":
                baseChance += 20;
                break;
            default:
                break;
        }
        int totalChance = baseChance + player.getLuck();

        if ((rand.nextInt(100) + 1 < totalChance)){
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
    /**
     * Configurethe enemy to be the final boss 
     * 
     * @param player a player class for its name
     */
    public void finalBoss(Player player){
        this.setLvl(9);
        this.setAc(this.rand.nextInt(10) + 7);
        this.setHp(22 * 9);
        this.diceType = 8;
        this.diceCount = 3;
        this.setBonus(5);
        this.poisonCount = 0;
        this.attack = dmg();
        this.setXp(33);
        this.setGold(1000);
        this.setLuck(10);
        this.data = "Final Boss";

        this.setBoss(true); // mark as boss

        
        this.setGold(this.getGold() * 2);
        this.setXp(this.getXp() * 2);
        this.setAc(this.getAc() + 2);
        this.diceCount += 1;
        this.setLuck(this.getLuck() + 10);
        this.setHp(this.getHp() + 5 * ((this.getLvl() / 2) + 1));
        this.setBonus(this.getBonus() + rand.nextInt(5) + 1);

        this.setName(Acolor.BRED.get() + "Final Boss " + Acolor.RESET.get() + Acolor.CYAN.get() + "Evil " + player.getName() + Acolor.RESET.get());
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

/**
 * Weapon class for spawning, creating and modifying wepons
 */
class Weapon{ // weapons and their abilities
    private String name;
    private int diceCount; //number of dices
    private int diceType; // type of dice
    private int data; // ID for the damage stats
    private int bonus;
    private int price;
    
    /**
     * returns a string of a wep for example = "dagger 1d4 + 0"
     * 
     * @return String of a wepon discription
     */
    public String toString(){
        return this.name + " " + this.diceCount + "d" + this.diceType + " " + this.bonus;
    }


    /**
     * a file of wepons list of potentioal wepons update the wep in memory
     * 
     * @param dmg id for damage stat
     * @param player player class foor luck and level
     */
    public void update(int dmg, Player player){  // will be called when buying or first opening the game
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
        checkBonusDice(player.getLvl(), player.getLuck());
        enhancerWep(player.getLvl(), player.getLuck());
        

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

    /**
     * checkes and add to this weapon a modifier to enhance it based on luck and player level
     * 
     * @param playerlvl is the player level value
     * @param luck is the player luck value
     */
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
                this.name = Acolor.GREEN.get() + "Epic " + Acolor.RESET.get() + this.name;
                this.bonus += 3;
                this.price += (int)(this.price * (0.70));
            }else if(chance + luck >= 90 && chance < 100 && playerlvl > 5){
                this.name = Acolor.PURPLE.get() + "Masterwork " + Acolor.RESET.get() + this.name;
                this.bonus += 4;
                this.price += (int)(this.price * (0.90));
            }else if (chance + luck == 100 && playerlvl > 6) {
                this.name = Acolor.CYAN.get() + "Legendary " + Acolor.RESET.get() + this.name;
                this.bonus += 5;
                this.diceCount += 1;
                this.price += this.price;
            }


        }else if(playerlvl < 3){
            if(chance > 93){
                this.name =Acolor.GREEN.get() + "Epic " + Acolor.RESET.get() + this.name;
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

    /**
     * Check and add a bonus diceCount to weapon if pass the mark using player level and luck
     * 
     * @param playerLevel the player level value 
     * @param luck the player luck value
     */
    private void checkBonusDice(int playerLevel, int luck){
        Random rand = new Random();
        int chance = rand.nextInt((100) + 1);

        if (playerLevel > 5){
            if (chance + luck == 100){
                this.name = Acolor.CYAN.get() + "Tripled " + Acolor.RESET.get() + this.name;
                this.diceCount *= 3;
                this.price += this.price;
            }else if (chance + luck > 80){
                this.name = Acolor.PURPLE.get() + "Doubled " + Acolor.RESET.get() + this.name;
                this.diceCount *= 2;
                this.price += (int)(this.price * (0.80));
            }
        
        }else if (playerLevel < 5){
            if (chance  + luck == 100){
                this.name = Acolor.PURPLE.get() + "Doubled " + Acolor.RESET.get() + this.name;
                this.diceCount *= 2;
                this.price += (int)(this.price * (0.80));
            }
        }   

    }
}

//=================== FEAT ENUM =====================

/**
 * The feat enum is for the diffrent feats in the game they
 * Are based on name, descriotion, category and rarity.
 * 
 * they apply their discription to the player and print to the user what they gained. [now in colors]
 */
enum Feat{
    POWER("POWER", "Bonus damage +1", "damage", 1){
        public void apply(Player p){
            p.setModifier(p.getModifier() + 1);
            System.out.println(Acolor.PURPLE.get() + "You gained POWER! Damage bonus +1" + Acolor.RESET.get());
        }
    },
    TANK("TANK", "Max HP +10", "health",  1){
        public void apply(Player p){
            p.setMaxHP(p.getMaxHP() + 10);
            p.setHp(p.getHp() + 10);
            System.out.println(Acolor.CYAN.get() + "You gained TANK! Max HP +10" + Acolor.RESET.get());
        }
    },
    LUCK("LUCK", "Chance +10%", "luck", 3){
        public void apply(Player p){
            p.setLuck(p.getLuck() + 10);
            System.out.println(Acolor.YELLOW.get() + "You gained LUCK! Chance +10%" + Acolor.RESET.get());
        }
    },
    DODGE("DODGE", "AC +1 (Armor Class)", "defense", 1){
        public void apply(Player p){
            p.setAc(p.getAc() + 1);
            System.out.println(Acolor.GREEN.get() + "You gained DODGE! AC +1" + Acolor.RESET.get());
        }
    },
    DICER("DICER", "One more for attack dice", "damage",  5){
        public void apply(Player p){
            p.setDiceCount(p.getDiceCount() + 1);
            System.out.println(Acolor.ORANGE.get() + "You gained DICER! One more for attack dice" + Acolor.RESET.get());
        }
    },
    RICH("RICH", "Get 300 gold", "gold", 1){
        public void apply(Player p){
            p.setGold(p.getGold() + 300);
            System.out.println(Acolor.BGREEN.get() + "You gained RICH! 300 gold" + Acolor.RESET.get());
        }
    },
    ALCHEMIST("ALCHEMIST", "Better potions", "health", 2){
        public void apply(Player p){
            p.setPotionHeal(p.getPotionHeal() + p.getMaxHP() / 10);
            System.out.println(Acolor.BGREEN.get() + "You gained ALCHEMIST! Better potions" + Acolor.RESET.get());
        }
    },
    FIREMAGIC("FIRE MAGIC", "Add 1d6 of fire damage", "damage", 5){
        public void apply(Player p){
            p.setFireMage(true);
            p.dicePool();
            p.setFireMage(false);
            System.out.println(Acolor.BRED.get() + "You gained FIRE MAGIC! Add 1d6 of fire damage" + Acolor.RESET.get());
        }
    },
    POISON("POISON", "Add a chance of 1d4 of poison damage", "damage", 3){
        public void apply(Player p){
            p.setPoisonMage(true);
            p.setCanPoison(true);
            p.dicePool();
            p.setPoisonMage(false);
            System.out.println(Acolor.BGREEN.get() + "You gained POISON! Add a consistent 1d4 of poison damage" + Acolor.RESET.get());
        }
    },
    WATERMAGE("Water Mage", "Add 1d8 of water damage", "damage", 5){
        public void apply(Player p){
            p.setWaterMage(true);
            p.dicePool();
            p.setWaterMage(false);
            System.out.println(Acolor.BLUE.get() + "You gained WATER MAGE! Add 1d8 of water damage" + Acolor.RESET.get());
        }
    },
    RECOVERY("Recovery", "heal 10% of max hp and the end of battles", "health", 2){
        public void apply(Player p){
            p.setRecovery(p.getRecovery() + 10);
            System.out.println(Acolor.RED.get() + "You gained RECOVERY! heal 10% of max hp and the end of battles" + Acolor.RESET.get());
        }
    };

    String name;
    String description;
    String category;
    int rarity;
    /**
     * How the feats should be used.
     * 
     * @param name String name for the feat
     * @param description String description for the feat
     * @param category String category for the feat
     * @param rarity int rarity value between 1-5
     */
    Feat(String name, String description, String category, int rarity){
        this.name = name;
        this.description = description;
        this.category = category;
        this.rarity = rarity;
    
    }


    public abstract void apply(Player p);

    /**
     * diffrent colors for diffrent rarity
     * 
     * print out to user the feat
     */
    public void display(){
    String rName;
        switch(rarity){
        case 2:
            rName = Acolor.BLUE.get() + "Uncommon" + Acolor.RESET.get();
            break;
        case 3:
            rName = Acolor.GREEN.get() + "Rare" + Acolor.RESET.get();
            break;
        case 4:
            rName = Acolor.YELLOW.get() + "Epic" + Acolor.RESET.get();
            break;
        case 5:
            rName = Acolor.PURPLE.get() + "Legendary" + Acolor.RESET.get();
            break;
        default:
            rName = "Common";
       
}
    System.out.println(Acolor.PURPLE.get() + name + Acolor.RESET.get() + " - " + description +   "(" + rName + ")" );
}
}

//========== PLAYER CLASS ============
/**
 * The player class the user playing this class
 * it extend the Entity class
 */
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
    private Map<Integer, Integer> dicepool = new HashMap<>(); // for magic type attacks
    private Map<Integer, Integer> poisonpool= new HashMap<>(); // for poison damage over time attacks


    private Random rand = new Random();

    /**
     * configure a new player with the user input as name
     * 
     * @param newName String user input name 
     */
    public void newPlayer(String newName){ //when starting new game init for the user
        this.setName(Acolor.ORANGE.get() + newName + Acolor.RESET.get());
        this.setLvl(1);
        this.setXp(0);
        this.MAXHP = rand.nextInt(6) + 1 + 40;
        this.setHp(this.MAXHP);
        this.setLuck(0);
        this.arm = new Armor();
        this.diceCount = 0;
        this.arm.updateArmor(this);
        this.setAc(10 + this.arm.getAc());
        this.wep = new Weapon();
        this.item = new Item();
        this.setGold(rand.nextInt(100 - (this.arm.getPrice()) + this.wep.getPrice()) + 26);    
        item.makePotion();
        this.bag.put(this.item.getName(), bag.getOrDefault(this.item.getName(), 0) + 1);
        wep.update(rand.nextInt(6) + 1, this);
        this.attack = dmg();
        if(newName.equals("GOD")){
            Cheats.GOD.active(this);
            Cheats.GOD.display();
            this.setHp(this.MAXHP);
        }
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

    /**
     * return an int value of how much xp the player need for level up!
     * @return int the value of how much xp needed to level up
     */
    public int xpNeeded(){ // for lvl ups
        int xpNeeded = (this.getLvl() + 1) * (this.getLvl() + 2) * 5;
        return xpNeeded;
    }
   // ========= DAMAGE METHODS ============ 
   /**
    * dice pool adds dices of magic attacks or DOTS if the player have them
    */
    public void dicePool(){
        if(this.fireMage == true){
            this.dicepool.put(6, this.dicepool.getOrDefault(6, 0) + 1);
        }else if(this.waterMage == true){
            this.dicepool.put(8, this.dicepool.getOrDefault(8, 0) + 1);
        }else if(this.poisonMage == true){
            this.poisonpool.put(4, this.poisonpool.getOrDefault(4, 0) + 1);
        }
    }
    /**
     * Calculate the damage the user is doing for each dice he have of poison
     * 
     * @return int sum of poison damage
     */
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

    /**
     * Return a string that show how much damage did each roll did of added dice.
     * in the color of damage it is done on.
     * 
     * @return a String of the damage that was done in the form of a dice. 
     */
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
                    diceString = Acolor.RED.get() + "d6" + Acolor.RESET.get();
                    break;
                case 8:
                    diceString = Acolor.BLUE.get() + "d8" + Acolor.RESET.get();
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

    /**
     * if a user have more dice in his dice pool roll for each dice he have 
     * and return the damage for each one
     * @return Int the sum of damage for each roll
     */
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
                        System.out.println(Acolor.RED.get() + "Fire damage: " + roll + Acolor.RESET.get());
                        break;
                    case 8:
                        System.out.println( Acolor.BLUE.get() +"Water damage: "  + roll + Acolor.RESET.get());
                        break;
                    default:
                        System.out.println("[DEBUG] Unknown damage type: " + roll);
                }
            }
        }
        return extraDamage;
    }

    
    @Override
    /**
     * Override the entity hitBonus() method and add the player bonus and his weapon bonus.
     * 
     * @return the int of the user modifier and player held weapon bonus
     */
    public int hitBonus() {
        return this.modifier + this.wep.getBonus();
    }

    /**
     * Calculate the damgage the user is doing for each dice the weapon has
     * and add the bonus dice if he has any, and hitBonus() if he has any
     * @return int total sum of damage done by this player
     */
    public int dmg(){ // main damage algo
        int damage = 0;;
        for (int i = 0; i < this.wep.getDiceCount() + this.diceCount; i++){
            damage += rand.nextInt(this.wep.getDiceType()) + 1;
        }
        damage += rollExtraDice();
        damage += hitBonus();
        return damage;
    }

    // =============== HEALTH METHODS ===============
    public boolean isLowHealth(){ 
        return this.getHp() > 0 && this.getHp() <= (int)(this.MAXHP * 0.45);
    }

    /**
     * check if the player has any potions
     * 
     * @return a boolean if he has any potions
     */
    public boolean hasAnyPotion(){
        return bag.getOrDefault("weak potion", 0) > 0
            || bag.getOrDefault("medium potion", 0) > 0
            || bag.getOrDefault("strong potion", 0) > 0
            || bag.getOrDefault("super potion", 0) > 0;
    }

    /**
     * use a potion based on merit, strongest to weakest and remove it from the bag Map.
     * 
     * @return String that represent which potion it used 
     */
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

    /**
     * if the player is low on health check if he has any potions
     * drink the potion and heal for the hp needed
     * 
     * if he dosent have print that he have no potion if low on health
     * 
     * if he dies but have revive use it and remove it from the bag
     */
    public void autoHealInFight(){
        if (this.isLowHealth()){
            if (this.hasAnyPotion()){
                String used = useBestPotion();
                System.out.println("You used a " + Acolor.RED.get() + used + Acolor.RESET.get() + ". HP is now " + Acolor.BLUE.get() + (String.valueOf(this.getHp())) + Acolor.RESET.get() + "/" + Acolor.GREEN.get() + (String.valueOf(this.MAXHP)) + Acolor.RESET.get() + ".");
            } else {
                System.out.println(Acolor.RED.get() + "No potions!" + Acolor.RESET.get());
            }
        }if (this.getHp() <= 0 && this.bag.getOrDefault("revive", 0) > 0){
            this.setHp(this.MAXHP / 2);
            this.bag.put("revive", this.bag.get("revive") - 1);
            System.out.println("You are not dead yet! " + Acolor.RED.get() + "used a revive" + Acolor.RESET.get() + ". HP is now " + Acolor.BLUE.get() + (String.valueOf(this.getHp())) + Acolor.RESET.get() + "/" + Acolor.GREEN.get() + (String.valueOf(this.MAXHP)) + Acolor.RESET.get() + ".");
        }
    }

    /**
     * heal the player for a precentege of max hp
     */
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
            System.out.println(Acolor.GREEN.get() + "lvl Up! you are lvl " + String.valueOf(this.getLvl()) + Acolor.RESET.get());
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

    /**
     * create a pool of feats based of rarity.
     * using a ist to add the feats to it and removing them based on weight '6'
     *  
     * @return List of feats
     */
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

    /**
     * Show the user the feats he can choose from 2 feats using the weighted list
     */
    private void offerFeatSelection(){
        // Randomly offer 2 out of 4 feats according to rarity
        List<Feat> weightedFeats = createWeightedFeatList();
        Feat feat1 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        Feat feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        while(feat2 == feat1){
            feat2 = weightedFeats.get(rand.nextInt(weightedFeats.size()));
        }
        
        System.out.println(Acolor.PURPLE.get() + "\n===== FEAT SELECTION =====" + Acolor.RESET.get());
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
    
    /**
     * check what the player input and apply the feat he chose
     * if not 1 or 2 choose 1
     * 
     * @param scan user input
     */
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

    /**
     * Show the user the feats he can choose from 3 feats using the weighted list
     */
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
        
        System.out.println(Acolor.CYAN.get() + "\n===== BOSS DEFEATED! FEAT REWARD =====" + Acolor.RESET.get());
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

    /**
     * check what the player input and apply the feat he chose
     * if not 1 or 3 choose 1
     * 
     * @param scan user input
     */
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

    /**
     * Display the feats the user aquired
     */
    public void displayFeats(){
        if(feats.isEmpty()){
            System.out.println("No feats acquired yet.");
            return;
        }
        System.out.println(Acolor.PURPLE.get() + "Your Feats:" + Acolor.RESET.get());
        for(Feat f : feats.keySet()){
            System.out.println("  - " + f.name + " (x" + feats.get(f) + ")");
        }
    }
    
    
    // =============== FINAL BOSS ===============
    /**
     * create the final boss enemy
     * 
     * @param enemy the enemy the user currently fighting
     * @return override the enemy as the final boss
     */
    public Enemy finalBossFight(Enemy enemy){
        
        if(this.getLvl() == 9 && (this.xpNeeded() - this.getXp()) <= enemy.getXp()){
            enemy.finalBoss(this);
        }
        return enemy;
    }

}
//=========== CHEATS ENUMS =========
/**
 * enums for cheats to modify the game using hp, gold, modifier, dice count, ac and luck.
 * and activate it upon the player
 */ 
enum Cheats{
    GOD("God mode", 100, 1000, 10, 3, 5, 5){
        public void active(Player p){
            p.setMaxHP(100);
            p.setGold(1000);
            p.setModifier(10);
            p.setDiceCount(3);
            p.setAc(p.getAc() + 5);
            p.setLuck(5);
        }
    };
    
    String name;
    int hp;
    int gold;
    int modifier;
    int diceCount;
    int ac;

    Cheats(String name, int hp, int gold, int modifier, int diceCount, int ac, int luck){
        this.name = name;
        this.hp = hp;
        this.gold = gold;
        this.modifier = modifier;
        this.diceCount = diceCount;
        this.ac = ac;
    }
    
    public abstract void active(Player p);

    public void display(){
    System.out.println(Acolor.BBLUE.get() + "CHEATS ACTIVE! " + this.name + Acolor.RESET.get());
    }
}

//=========== ITEM CLASS ==========
/**
 * Item class for potions and revives
 */
class Item{ // potions, nothing else
    private String name;
    private int price;
    private int heal;
    private Random rand = new Random();
    /**
     * configure an item using chance
     */
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
/**
 * Armor class for the armor the player will use
 * modifying the ac and adding to the ac of an Entity class (currently only player)
 */
class Armor{ // same logic as wep except for protaction
    private String name;
    private int ac;
    private int price;
    private Random rand = new Random();
    /**
     * configures the armor using the player class for luck and level
     * @param player a player clsss for the _enhancerArm method
     */
    public void updateArmor(Player player){
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
    }__enhancerArm(player.getLvl(), player.getLuck());
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
    
    /**
     * check if the armor get enhanced modifiers using the player level and luck
     * 
     * @param playerlvl int of player level
     * @param luck int of player luck
     */
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
                this.name = Acolor.GREEN.get() + "Epic " + Acolor.RESET.get() + this.name;
                this.ac += 3;
                this.price += (int)(this.price * (0.70));
            }else if(chance >= 90 && chance < 100 && playerlvl > 5){
                this.name = Acolor.PURPLE.get() + "Masterwork " + Acolor.RESET.get() + this.name;
                this.ac += 4;
                this.price += (int)(this.price * (0.90));
            }else if(chance == 100 && playerlvl > 6){
                this.name = Acolor.CYAN.get()  + "Legendary " + Acolor.RESET.get() + this.name;
                this.ac += 5;
                this.price += this.price;
            }
        }
        else if(playerlvl < 3){
            if(chance > 93){
                this.name = Acolor.GREEN.get() + "Epic " + Acolor.RESET.get() + this.name;
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
/**
 * A shop class for the player to visit and buy items, weapons and armor
 */
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


    /**
     * create and check if the player can buy the weapon if they can
     * check their input if they want to and change it accordingly
     * 
     * @param wepName the weapon it creats
     * @param scan the player input
     * @param user the current player playing
     */
    public void canBuyWeapon(Weapon wepName, Scanner scan, Player user){ // check if user can buy wep
        System.out.println("There is " + wepName.getName() + " its price is " + wepName.getPrice());
        System.out.println("You have " + user.getGold());
        if(user.getGold() >= wepName.getPrice()){
            System.out.println("Do you want to buy this weapon? " + wepName.getName() + " y/n");
            System.out.println("Damage die: " + wepName.getDiceCount() + "d" + wepName.getDiceType() + "+" + " " + wepName.getBonus());
            System.out.println("Compaer to yours: " + user.getWeapon() + " " + user.getBonus());
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                user.setGold(user.getGold() - wepName.getPrice());
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
    }

    /**
     * create and check if the player can buy the item, if they can
     * check their input if they want to and change it accordingly
     * 
     * @param potion the item it creates
     * @param scan the user input
     * @param user the current player playing
     */
    public void canBuyItem(Item potion, Scanner scan, Player user){ // same as before but for item
        System.out.println("There is " + potion.getName() + " its price is " + potion.getPrice());
        System.out.println("You have " + user.getGold());
        if(user.getGold() >= potion.getPrice()){
            System.out.println("Do you want to buy this potion? " + potion.getName() + " y/n");
            System.out.println("Heal for: " + potion.getHeal());
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                user.setGold(user.getGold() - potion.getPrice());
                System.out.println("You bought " + potion.getName());
                user.getBag().put(potion.getName(), user.getBag().getOrDefault(potion.getName(), 0) + 1);
            }else{
                System.out.println("You didnt buy it!");
            }
        }else{
            System.out.println("Insufficiant gold");
        }
    }    

    /**
     * Create an armor and check if the user have enough gold to buy it
     * if they do check their input if they want it and change it accordingly
     * 
     * @param armName - the armor thats being created
     * @param scan - the player input 
     * @param user - the current playing player
     * @return
     */
    public void canBuyArmor(Armor armName, Scanner scan, Player user){ // same as before but for armor
        if (this.arm.getAc() == 0){
            this.arm = new Armor();
        }
        System.out.println("There is " + armName.getName() + " its price is " + Acolor.YELLOW.get() + (String.valueOf(armName.getPrice())) + Acolor.RESET.get());
        System.out.println("You have " + Acolor.YELLOW.get() + (String.valueOf(user.getGold()) + Acolor.RESET.get()));
        if(user.getGold() >= armName.getPrice()){
            System.out.println("Do you want to buy this armor? " + armName.getName() + " y/n");
            System.out.println("Armor class: " + armName.getAc());
            String input1 = scan.next();
            if(input1.equals("y") || input1.equals("yes")){
                user.setGold(user.getGold() - armName.getPrice());
                System.out.println("You bought " + armName.getName());
                user.setAc(user.getAc() - user.getArmor().getAc()); 
                user.setArmor(armName);
                user.setAc(user.getAc() + armName.getAc());
            }else{
                System.out.println("You didnt buy it!");
            }
        }
    }
    
}

//================FIGHT CLASS================

/**
 * the fight method main controller of the game 
 * handle game loop  combat flow and shop interactions
 */
public class Fight{
    //=============== POISON METHODS ==================
    /**
     * a method for the player if they can apply poison to try and apply the poison to the enemy
     * 
     * @param player current playing player 
     * @param enemy current enemy
     */
    public static void tryApplyPoison(Player player, Enemy enemy){
        Random rand = new Random();
        int d100 = rand.nextInt(100) + 1;
        if((player.canCastPoison() == true && (d100 + player.getLuck()) > 80)){
            enemy.setPoisoned(true);
            enemy.setPoisonCount(3); //last 3 ticks
            System.out.println(enemy.getName() + Acolor.BGREEN.get() + " Is poisoned!" + Acolor.RESET.get());
        }
    }

    /**
     * check if the enemy is poisoned if yes apply the current player poison damage
     * to the enemy health, also check the enemy poison count and lowers it
     * 
     * 
     * @param player current playing player
     * @param enemy current enemy
     */
    public static void processPoison(Player player, Enemy enemy){
        
        if(!enemy.isPoisoned()){
            return;
        }

        int hit = player.poisondmg();
        enemy.setHp(enemy.getHp() - hit);
        enemy.setPoisonCount(enemy.getPoisonCount() - 1);
        System.out.println(enemy.getName() + Acolor.BGREEN.get() + " Is poisoned! and was hit by: " + hit + Acolor.RESET.get() + " hp is : " + enemy.getHp());
        if(enemy.getPoisonCount() <= 0){
            enemy.setPoisoned(false);
            System.out.println(enemy.getName() + Acolor.BGREEN.get() + " Is not poisoned anymore!" + Acolor.RESET.get());
        }
    }


    //======== METHOD FOR ATTACKING ============
    /**
     * the main attacking method using rolls of chance of d20 using 2 entities
     * using rolls and bonuses against ac if it pass lower the defender hp according to the attack
     * also have a crit succsus and fail if roll is 1 or 20 
     * 
     * @param roll int value of the roll 
     * @param att Entity the one who attacks
     * @param def Entity the one who defends
     * @return the entetity health
     */
    public static int attack(int roll, Entity att, Entity def){ // takes in the "dice" roll, attacker attack, attacked ac, attacked hp, name of the attacker, name of the attacked and returning new attacked hp
        int crit = 20 - (att.getLuck() / 10);
        if (((roll + att.hitBonus()) >= def.getAc() && def.getHp() > 0 && roll != 1) || roll >= (crit)){
            if(roll >= crit){
                 //Critical double damgae
                int dmg = att.dmg() * 2;
                def.setHp(def.getHp() - dmg);
                System.out.println(Acolor.RED.get() + "CRITICAL!!! " + Acolor.RESET.get() + att.getName() + " hit! " + def.getName() + " for: " + Acolor.RED.get() + dmg + Acolor.RESET.get() + " hp is now at: " + def.getHp() + " points!");
            }else{
                int hit = att.dmg();
                def.setHp(def.getHp() - hit);
                System.out.println(att.getName() + " hit " + def.getName() + " for: " + Acolor.RED.get() + hit + Acolor.RESET.get() + " " + def.getName() + " hp is: " + def.getHp() + " points!");

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
    /**
     * a wait time wrapper using thread sleep to slow the game 
     * and make a little bit of wait
     * 
     * @param time int for how much time to wait (1000 = 1 second)
     */
    public static void wait(int time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException a){
            System.out.println("Thread was interupted");
        }
    }
    
    // ============= Main ======================

    /**
     * the main method, every thing runs here
     * 
     * @param args arguments (not used)
     */
    public static void main(String[] args){
        Random rand = new Random();
        Scanner scan = new Scanner(System.in); // for user inputs
        Player player = new Player(); // user player
        String userAnsware1 = "PLACE HOLDER"; // for if the user wants to play
        final String DEF_NAME = "hero";
        boolean gameWon = false;
        boolean finalBossSpawned = false;
        SwingUtilities.invokeLater(() ->{
           GameWindow game =  new GameWindow("Game", true);
            game.print("ENTER_NAME");

        });
        
        

    
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
        if(player.getName().length() > 30){
            player.setName(player.getName().substring(0, 30));
        }

        System.out.println("Hello " + Acolor.ORANGE.get() + player.getName() + Acolor.RESET.get() + ".");
        System.out.println("This are your stats: ");
        
        wait(500);
        
        System.out.println("hp: " + Acolor.BLUE.get() + player.getHp() + Acolor.RESET.get());
        System.out.println("ac: " + Acolor.RED.get() + player.getAc() + Acolor.RESET.get());
        System.out.println("Weapon: " + player.getWeapon().getName() + " damage die: " + player.getWeapon().getDiceCount() + "d" + player.getWeapon().getDiceType());
        System.out.println("Armor: " + player.getArmor().getName() + " ac: " + player.getArmor().getAc());
        System.out.println("Gold: " + Acolor.YELLOW.get() + player.getGold() + Acolor.RESET.get());
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
        while (player.getHp() > 0 && !gameWon){
            Enemy e = new Enemy(); //spawn enemy
            
            if(!finalBossSpawned){
                int xpLeft = player.xpNeeded() - player.getXp();
        
                if (player.getLvl() == 9 && xpLeft < 33){
                    e.finalBoss(player);
                    finalBossSpawned = true;
                }else{
                e.lvlBased(player.getLvl());
                if(player.getLvl() >= 3){
                    e.isBoss(false);
                }
            }
        }else{
            e.lvlBased(player.getLvl());
            if(player.getLvl() >= 3){
                e.isBoss(false);
            }
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
            
            if(player.getHp() <= 0){
                if(player.getBag().getOrDefault("revive", 0) > 0){
                    player.setHp(player.getMaxHP() / 2);
                    player.getBag().put("revive", player.getBag().get("revive") - 1);
                    System.out.println("You have been revived! " + Acolor.BBLUE.get() + player.getHp() + Acolor.RESET.get());
                }else {
                    System.out.println("You have been defeated!");
        
                    wait(1000);

                    System.out.println("========= "+ Acolor.RED.get() + "R I P" + Acolor.RESET.get() + " =========");
                    wait(300);
                    System.out.println("Name: " + player.getName());
                    System.out.println("lvl: " + Acolor.BLUE.get() + player.getLvl() + Acolor.RESET.get());
                    System.out.println("hp: " + Acolor.GREEN.get() + player.getMaxHP() + Acolor.RESET.get());
                    System.out.println("ac: " + player.getAc());
                    System.out.println("gold: " + Acolor.YELLOW.get() + (String.valueOf(player.getGold())) + Acolor.RESET.get());
                    System.out.println("You have fought: " + fightCount + " fights");
                    System.out.println("was slayed by " + Acolor.RED.get() + e.getName() + Acolor.RESET.get());
                    wait(300);
                    System.out.println("=========================");

                    wait(1000);

                    System.exit(0);
                }

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
                if(e.getData().equals("Final Boss")){
                    gameWon = true;
                }
                System.out.println("You have defeated the enemy! \n");
                player.setXp(player.getXp() + e.getXp());
                player.setGold(player.getGold() + e.getGold());
                fightCount += 1;
                e.dropChance(player.getHp(), player.getLvl(), player.getBag(), player);;
                player.recoverAfterBattle();
                System.out.println("\nYou Got " + e.getXp() + " xp and " + Acolor.YELLOW.get() + (String.valueOf(e.getGold())) + Acolor.RESET.get() + " gold!\n");
                player.checkLvlUp();
                
                // Check if boss was defeated and offer feat
                if(e.isBossEnemy() == true){
                    player.offerBossFeat();
                }
                
                System.out.println("You are lvl: " + Acolor.PINK.get() + player.getLvl() + Acolor.RESET.get() + ". " + Acolor.BORANGE.get() + (player.xpNeeded() - player.getXp()) + Acolor.RESET.get() + " needed more xp to lvl up.  with a " + player.getWeapon().getName() + " that does: " + (player.getWeapon().getDiceCount() + player.getDiceCount()) + "d" + player.getWeapon().getDiceType() + " + " + (player.getWeapon().getBonus() + player.getModifier()) + " " + player.dicePoolToString() + " damage." );
                System.out.println("You also have: " + Acolor.GREEN.get() + player.getBag() + Acolor.RESET.get() + "\n");
                System.out.println("Armor: " + player.getArmor().getName() + " that return currents: " + Acolor.BBLUE.get() + player.getArmor().getAc() + Acolor.RESET.get() + " ac.\n");
                System.out.println("You have: " + Acolor.YELLOW.get() + (String.valueOf(player.getGold())) + Acolor.RESET.get() + " gold.\n");
                player.displayFeats();
                System.out.println();
                
                Shop shop = new Shop();
                shop.getItem().makePotion();
                shop.getWeapon().update(rand.nextInt(player.getLvl() + 5) + 1, player);
                shop.getArmor().updateArmor(player);
                System.out.println("Please enter any key and press enter to continue.");
                
                scan.next();
                
                // Handle pending feat selections if any
                player.chooseFeatureFromInput(scan);
                if(e.isBossEnemy()){
                    player.chooseBossFeat(scan);
                }
                
                System.out.println("In the shop there are:");
                System.out.println(shop.getItem().getName() + " priced at: " + Acolor.YELLOW.get() + (String.valueOf(shop.getItem().getPrice())) + Acolor.RESET.get());
                System.out.println(shop.getWeapon().getName() + " priced at: " + Acolor.YELLOW.get() + (String.valueOf(shop.getWeapon().getPrice())) + Acolor.RESET.get());
                System.out.println(shop.getArmor().getName() + " priced at: " + Acolor.YELLOW.get() + (String.valueOf(shop.getArmor().getPrice())) + Acolor.RESET.get());
                System.out.println("Do you want to go to the shop? y/n");
                String shopInput = scan.next();
                if(shopInput.equals("y") || shopInput.equals("yes")){
                    shop.canBuyItem(shop.getItem(), scan, player);
                    shop.canBuyWeapon(shop.getWeapon(), scan, player);
                    shop.canBuyArmor( shop.getArmor(), scan, player);
            }
        }
    }

    if(gameWon){
    int score = (player.getXp() + ratCount + slimeCount + (wolfCount * 2) + (goblinCount * 3) + (dragonCount * 5) + player.getFeats().size() + player.getGold());
    System.out.println("======= " + Acolor.GREEN.get() + "W I N " + Acolor.RESET.get() + "=======");
    System.out.println("You have won the game!");
    System.out.println(player.getName() + " have fought: " + fightCount + " fights");
    System.out.println(player.getName() + " have: " + player.getGold() + " gold");
    System.out.println(player.getName() +  " You have: " + player.getXp() + " xp");
    System.out.println("Items: " + player.getArmor().getName() + ", " + player.getWeapon().getName() + ".");
    System.out.println("You have defeated: ");
    System.out.println(Acolor.YELLOW.get() + "Rats: " + ratCount + Acolor.RESET.get());
    System.out.println(Acolor.BLUE.get() + "Slimes: " + slimeCount + Acolor.RESET.get());
    System.out.println(Acolor.RED.get() + "Wolves: " + wolfCount + Acolor.RESET.get());
    System.out.println(Acolor.BGREEN.get() + "Goblins: " + goblinCount + Acolor.RESET.get());
    System.out.println(Acolor.PURPLE.get() + "Dragons: " + dragonCount + Acolor.RESET.get());
    System.out.println("\n" + Acolor.CYAN.get() + "Feats Acquired:" + Acolor.RESET.get());
    player.displayFeats();
    System.out.println("\nYour score is: " + score);
    System.out.println("=====================");
    }
}
}
