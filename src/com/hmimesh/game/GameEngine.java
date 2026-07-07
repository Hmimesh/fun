package com.hmimesh.game;

import java.util.Random;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Core game logic controller for Dungo.
 *
 * <p>Creates the {@link GameWindow}, registers all state-transition callbacks,
 * and drives the combat loop via a {@link SwingWorker} so the UI stays
 * responsive during fights.
 *
 * <p>Game flow:
 * <ol>
 *   <li>ENTER_NAME — player types their name; {@link Player} is built.</li>
 *   <li>READY — player confirms they are ready to enter the dungeon.</li>
 *   <li>DOOR_CHOOSE — player picks easy (left) or hard (right) door.</li>
 *   <li>EASY_FIGHT / HARD_FIGHT — combat loop runs in background thread.</li>
 *   <li>AFTER_BATTLES — player chooses to continue or rest.</li>
 *   <li>GAME_OVER — player died; restart or quit.</li>
 * </ol>
 *
 * @author Ben Farjun
 */
class GameEngine {

    private Player     _player;
    private Enemy      _enemy;
    private Shop       _shop;
    private GameWindow _game;

    private boolean gameOverState      = false;
    private boolean finalBossSpawned   = false;

    private static final String DEFAULT_GAME_NAME = "Dungo";

    // ─── Enemy encounter counters (for future scene variety) ─────────────────
    private int _fightCount  = 0;
    private int _slimeCount  = 0;
    private int _wolfCount   = 0;
    private int _goblinCount = 0;
    private int _dragonCount = 0;
    private int _ratCount    = 0;

    // ─── Constructor ─────────────────────────────────────────────────────────

    public GameEngine() {
        _game = new GameWindow(DEFAULT_GAME_NAME, true, name -> {
            // ENTER_NAME callback: build the player, then hand off to READY
            this._player = new Player();
            this._player.newPlayer(name);
            _game.setPlayer(this._player);

            _game.setState(GameState.READY);
            _game.setScene(
                    "You find yourself in a dimly lit dungeon, the air thick with the scent of damp stone and ancient secrets.\n"
                  + "The walls are adorned with faded tapestries depicting long-forgotten battles,\n"
                  + "and the flickering torchlight casts eerie shadows.\n"
                  + "In the distance you hear dripping water and the distant scurrying of unseen creatures.\n\n"
                  + "Name:   " + this._player.getName()            + "\n"
                  + "Race:   " + this._player.getRace()            + "\n"
                  + "HP:     " + this._player.getHp() + "/" + this._player.getMaxHP() + "\n"
                  + "AC:     " + this._player.getAc()              + "\n"
                  + "Weapon: " + this._player.getWeapon()          + "\n"
                  + "Armor:  " + this._player.getArmor().getName() + "\n"
                  + "Mod:    " + this._player.getModifier()        + "\n"
                  + "Luck:   " + this._player.getLuck()            + "\n"
                  + "Gold:   " + this._player.getGold()            + "\n\n"
                  + "Are you ready to start your adventure? (y/n)"
            );
            _game.print("Welcome " + this._player.getName() + "! Are you ready? (y/n)");
        });

        // READY callback: confirms intent to enter the dungeon
        _game.setOnReady(answer -> {
            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                _game.setState(GameState.DOOR_CHOOSE);
                _game.print(this._player.getName() + " — welcome to the Dungeon! Good luck!");
                doorChooser();
            } else if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
                _game.setState(GameState.ENTER_NAME);
                _game.setScene(_game.getDefaultScene());
                _game.print("Let's start over. Please enter your name:");
                _game.setIsWaitingForPlayer(true);
            } else {
                _game.print("Please answer with yes or no.");
            }
        });

        // DOOR_CHOOSE callback: pick easy or hard fight
        _game.setOnDoorChoose(answer -> {
            if (answer.equalsIgnoreCase("1") || answer.equalsIgnoreCase("left") || answer.equalsIgnoreCase("l")) {
                _game.setState(GameState.EASY_FIGHT);
                fightloop();
            } else if (answer.equalsIgnoreCase("2") || answer.equalsIgnoreCase("right") || answer.equalsIgnoreCase("r")) {
                _game.setState(GameState.HARD_FIGHT);
                fightloop();
            } else {
                _game.print("Please answer: 1 - left (easy) or 2 - right (hard).");
            }
        });

        // AFTER_BATTLES callback: continue to shop or rest
        _game.setOnAfterBattles(answer -> {
            if (_game.getState() == GameState.AFTER_BATTLES) {
                if (answer.equalsIgnoreCase("1") || answer.equalsIgnoreCase("Shop") || answer.equalsIgnoreCase("s")) {
                    _game.setState(GameState.SHOP);
                    shop(_game, _player);
                } else if (answer.equalsIgnoreCase("2") || answer.equalsIgnoreCase("Rest") || answer.equalsIgnoreCase("r")) {
                    _game.setState(GameState.REST);
                    int heal = _player.recoverAfterBattle();
                    _game.print("You chose to rest. Your recovered: " + heal + "your HP is now: " + _player.getHp() + "/" + _player.getMaxHP() + "HP");
                    wait(1000);
                    doorChooser();
                } else {
                    _game.print("Please answer with 1 - Shop or 2 - Rest.");
                }
            }
        });


        // GAME_OVER callback: restart or exit
        _game.setOnGameOver(answer -> {
            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                _game.setState(GameState.ENTER_NAME);
                _game.setScene(_game.getDefaultScene());
                _game.print("Let's start over. Please enter your name:");
                _game.setIsWaitingForPlayer(true);
            } else if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
                _game.setState(GameState.REST);
                _game.print("Thanks for playing! See you next time.");
                System.exit(0);
            } else {
                _game.print("Please answer with yes or no.");
            }
        });

        // FEAT_CHOOSE callback: player picks one of the two offered feats
        _game.setOnLevelUp(answer -> {
            if (!_player.hasPendingFeat()) return;
            if (answer.equals("1") || answer.equals("2")) {
                Feat chosen = _player.getPendingFeat(answer.equals("1") ? 0 : 1);
                _player.applyPendingFeatByChoice(answer);
                _game.print("You chose: " + chosen.name + "!");
                _game.print("  → " + chosen.description);
                _game.setState(GameState.AFTER_BATTLES);
                _game.print("Where to next? \n  1 - Shop\n  2 - Rest\nType 1 or 2 to choose:");
            } else {
                _game.print("Please type 1 or 2 to choose your feat.");
            }
        });

        // Kick off the game
        _game.setScene(_game.getDefaultScene());
        _game.setState(GameState.ENTER_NAME);
        _game.print("Please enter your name:");
        _game.setIsWaitingForPlayer(true);
    }



    // ─── Getters / setters ───────────────────────────────────────────────────

    public Player  getPlayer()  { return this._player; }
    public Enemy   getEnemy()   { return this._enemy;  }
    public Shop    getShop()    { return this._shop;   }
    public boolean isGameOver() { return this.gameOverState; }

    public void setPlayer(Player player)  { this._player    = player; }
    public void setEnemy(Enemy enemy)     { this._enemy     = enemy;  }
    public void setShop(Shop shop)        { this._shop      = shop;   }
    public void setGameOver(boolean over) { this.gameOverState = over; }

    // ─── Poison mechanics ────────────────────────────────────────────────────

    /**
     * Applies one tick of poison damage to the enemy if it is currently
     * poisoned, then decrements the poison counter.
     *
     * @param player the player (provides poison damage value)
     * @param enemy  the poisoned enemy
     * @param game   the game window (for logging)
     */
    public static void processPoison(Player player, Enemy enemy, GameWindow game) {
        if (!enemy.isPoisoned()) return;
        int hit = player.poisondmg();
        enemy.setHp(enemy.getHp() - hit);
        enemy.setPoisonCount(enemy.getPoisonCount() - 1);
        game.print(enemy.getName() + " is poisoned! Hit for " + hit + " — HP is now: " + enemy.getHp());
        if (enemy.getPoisonCount() <= 0) {
            enemy.setPoisoned(false);
            game.print(enemy.getName() + " is no longer poisoned!");
        }
    }

    /**
     * Attempts to apply poison to the enemy when the player has the POISON
     * feat.  Uses a d100 roll modified by player luck.
     *
     * @param player the player (checked for poison capability)
     * @param enemy  the current enemy
     * @param game   the game window (for logging)
     */
    public static void tryApplyPoison(Player player, Enemy enemy, GameWindow game) {
        Random rand = new Random();
        int d100    = rand.nextInt(100) + 1;
        if (player.canCastPoison() && (d100 + player.getLuck()) > 80) {
            enemy.setPoisoned(true);
            enemy.setPoisonCount(3); // lasts 3 ticks
            game.print(enemy.getName() + " is poisoned!");
        }
    }

    // ─── Attack ──────────────────────────────────────────────────────────────

    /**
     * Resolves one attack roll between an attacker and a defender.
     *
     * <p>Rules:
     * <ul>
     *   <li>Roll 1 is always a critical miss (no damage).</li>
     *   <li>Roll ≥ crit threshold is a critical hit (double damage).</li>
     *   <li>Otherwise hits if {@code roll + hitBonus ≥ defenderAC}.</li>
     * </ul>
     * After a player hit, {@link #tryApplyPoison} is called if applicable.
     *
     * @param roll the d20 roll result
     * @param att  the attacking entity
     * @param def  the defending entity
     * @return the defender's HP after the attack
     */
    public int attack(int roll, Entity att, Entity def) {
        int minCrit = 20;
        // Higher luck slightly lowers the crit threshold (more crit chances)
        int crit = Math.min(20 - (att.getLuck() / 10), minCrit);

        _game.print(att.getName() + " rolled " + roll
                + " vs " + def.getName() + "'s AC " + def.getAc()
                + " (hit bonus: " + att.hitBonus() + ")");

        if (((roll + att.hitBonus()) >= def.getAc() && def.getHp() > 0 && roll != 1)
                || roll >= crit) {
            if (roll >= crit) {
                int dmg = att.dmg() * 2;
                def.setHp(def.getHp() - dmg);
                _game.print("CRITICAL HIT! " + att.getName()
                        + " hits " + def.getName() + " for " + dmg
                        + " — HP: " + def.getHp());
            } else {
                int hit = att.dmg();
                def.setHp(def.getHp() - hit);
                _game.print(att.getName() + " hits " + def.getName()
                        + " for " + hit + " — HP: " + def.getHp());
            }
            // Attempt poison application on player attacks
            if (att instanceof Player && def instanceof Enemy) {
                tryApplyPoison((Player) att, (Enemy) def, _game);
            }
        } else if (roll == 1) {
            _game.print("CRITICAL MISS for " + att.getName() + "!");
        } else {
            _game.print(att.getName() + " missed " + def.getName() + "!");
        }
        return def.getHp();
    }

    // ─── Timing ──────────────────────────────────────────────────────────────

    /**
     * Sleeps the current thread for {@code time} milliseconds.
     * Used to pace the combat loop output.
     *
     * @param time milliseconds to wait
     */
    public static void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ─── Door chooser ────────────────────────────────────────────────────────

    /** Prints the door-selection prompt to the log. */
    public void doorChooser() {
        _game.print("In front of you are two doors:");
        _game.print("  1 - Left  (easy battle)");
        _game.print("  2 - Right (hard battle)");
        _game.print("Which door do you choose?");
    }

    // ─── Combat loop ─────────────────────────────────────────────────────────

    /**
     * Runs one full combat encounter in a background SwingWorker thread so the
     * Swing EDT remains responsive.
     *
     * <p>The enemy is generated based on the current game state
     * (EASY_FIGHT / HARD_FIGHT) and the player's level.  Each round both
     * sides attack; initiative is determined by level + luck.  After each
     * round, poison is applied if the enemy is poisoned, and the player
     * auto-heals if low on HP.
     */
    public void fightloop() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                int count = 0;
                Random rand = new Random();

                // Spawn the enemy based on chosen difficulty
                _enemy = new Enemy();
                boolean hard = (_game.getState() == GameState.HARD_FIGHT);
                _enemy.lvlBased(_player.getLvl(), hard);

                _game.print("As you enter the room you see a "
                        + _enemy.getName() + " (lvl " + _enemy.getLvl()
                        + ") with " + _enemy.getHp() + " HP and " + _enemy.getAc() + " AC!");
                _game.print("Get ready to fight!");

                while (_player.getHp() > 0 && _enemy.getHp() > 0) {
                    int roll      = rand.nextInt(20) + 1;
                    int enemyRoll = rand.nextInt(20) + 1;
                    count++;
                    Thread.sleep(1500);
                    _game.print("══════════════ ROUND " + count + " ══════════════");

                    // Initiative: higher (level + luck) goes first
                    if (_player.getLvl() + _player.getLuck() >= _enemy.getLvl() + _enemy.getLuck()) {
                        attack(roll, _player, _enemy);
                        if (_enemy.getHp() > 0) attack(enemyRoll, _enemy, _player);
                    } else {
                        attack(enemyRoll, _enemy, _player);
                        if (_player.getHp() > 0) attack(roll, _player, _enemy);
                    }

                    processPoison(_player, _enemy, _game);

                    // Auto-heal when HP is low
                    if (_player.autoHealInFight()) {
                        String used = _player.useBestPotion();
                        _game.print("You used " + used + " to heal! HP is now: " + _player.getHp());
                    } else if (_player.isLowHealth()) {
                        _game.print("You are low on health and have no potions!");
                    }

                    _game.print("══════ End of round " + count + " ══════");
                    Thread.sleep(1500);

                    if (_player.getHp() <= 0) {
                        _game.print("You have been defeated by " + _enemy.getName() + " — better luck next time!");
                        setGameOver(true);
                        // showGameOverScreen modifies the UI — must run on the EDT
                        SwingUtilities.invokeLater(() -> showGameOverScreen());
                        break;
                    } else if (_enemy.getHp() <= 0) {
                        _game.print("You defeated " + _enemy.getName() + " — congratulations!");
                        _player.setXp(_player.getXp() + _enemy.getXp());
                        _player.setGold(_player.getGold() + _enemy.getGold());
                        _player.checkLvlUp(_game);
                        // After victory: show feat selection or go straight to AFTER_BATTLES
                        SwingUtilities.invokeLater(() -> {
                            if (_player.hasPendingFeat()) {
                                Feat f1 = _player.getPendingFeat(0);
                                Feat f2 = _player.getPendingFeat(1);
                                _game.setState(GameState.FEAT_CHOOSE);
                                _game.print("══════ FEAT SELECTION ══════");
                                _game.print("[1]  " + f1.name + " — " + f1.description);
                                _game.print("[2]  " + f2.name + " — " + f2.description);
                                _game.print("Type 1 or 2 to choose:");
                            } else {
                                _game.setState(GameState.AFTER_BATTLES);
                                _game.print("Where to next\n");
                                _game.print("  1 - Shop");
                                _game.print("  2 - Rest");
                                _game.print("Type 1 or 2 to choose:");
                            }
                        });
                        break;
                    } else {
                        _game.print("The fight continues...");
                    }
                }
                return null;
            }
        };
        worker.execute();
    }

    // ─── Game-over screen ────────────────────────────────────────────────────

    /**
     * Updates the scene and state to show the player's death summary.
     * Must be called on the Event Dispatch Thread.
     */
    private void showGameOverScreen() {
        _game.setState(GameState.GAME_OVER);
        _game.setScene(
            "══════════════ RIP ══════════════\n"
          + "Here lies " + this._player.getName()  + "\n"
          + "Level:     " + this._player.getLvl()  + "\n"
          + "Race:      " + this._player.getRace() + "\n"
          + "Feats:     " + this._player.getFeats() + "\n"
          + "Inventory: " + this._player.getWeapon()
                          + " and " + this._player.getArmor().getName() + "\n"
          + "\nWho fought bravely but was defeated in the dungeon.\n"
          + "Defeated by " + this._enemy.getName()
                           + " (level " + this._enemy.getLvl() + ").\n"
          + "══════════════════════════════════\n"
          + "DO YOU WANT TO START OVER? (y/n)"
        );
        _game.print("Do you want to start over? (y/n)");
    }


    public void shop(GameWindow _game, Player _player) {
        _game.setState(GameState.SHOP);
        _shop = new Shop();

        // Generate items for sale
        Weapon wepForSale = new Weapon();
        wepForSale.update(new Random().nextInt(13) + 1, _player);
        Item itemForSale = new Item();
        itemForSale.makePotion();

        // Show the shop in the scene panel
        _game.setScene(
            "══════════ SHOP ══════════\n"
        + "1 - Weapon: " + wepForSale + " — " + wepForSale.getPrice() + "g\n"
        + "2 - Potion: " + itemForSale.getName() + " — " + itemForSale.getPrice() + "g\n"
        + "3 - Leave shop\n"
        + "Your gold: " + _player.getGold() + "g\n"
        + "═══════════════════════════"
        );
        _game.print("What do you want to buy? (1/2/3)");

        // Register the callback
        _game.setOnShop(answer -> {
            if (answer.equals("1")) {
                if (_player.getGold() >= wepForSale.getPrice()) {
                    _player.setGold(_player.getGold() - wepForSale.getPrice());
                    _player.setWeapon(wepForSale);
                    _game.print("You bought " + wepForSale.getName() + "!");
            } else {
                _game.print("Insufficient gold!");
                }
            } else if (answer.equals("2")) {
                if (_player.getGold() >= itemForSale.getPrice()) {
                    _player.setGold(_player.getGold() - itemForSale.getPrice());
                    _player.getBag().put(itemForSale.getName(),
                    _player.getBag().getOrDefault(itemForSale.getName(), 0) + 1);
                    _game.print("You bought " + itemForSale.getName() + "!");
                } else {
                    _game.print("Insufficient gold!");
                }
            } else if (answer.equals("3")) {
                _game.print("You leave the shop.");
                goToDoors();
            } else {
                _game.print("Please type 1, 2, or 3.");
            }
        });
    }

    private void goToDoors() {
        _game.setState(GameState.DOOR_CHOOSE);
        doorChooser();
    }
}