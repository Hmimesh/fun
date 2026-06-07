package com.hmimesh.game;

import java.util.Scanner;

/**
 * The in-game shop, visited between battles.
 *
 * <p>Holds one of each item type (weapon, armour, potion).  The
 * {@code canBuy*} methods display stock and handle the purchase flow via a
 * terminal {@link Scanner}.
 *
 * <p><b>Note:</b> The shop's Scanner-based purchase flow is a carry-over from
 * the original terminal version.  Integration with the Swing GUI is planned
 * for a future update.
 *
 * @author Ben Farjun
 */
class Shop {

    private Item   item = new Item();
    private Weapon wep  = new Weapon();
    private Armor  arm  = new Armor();

    // ─── Getters ────────────────────────────────────────────────────────────

    public Item   getItem()   { return this.item; }
    public Weapon getWeapon() { return this.wep;  }
    public Armor  getArmor()  { return this.arm;  }

    // ─── Purchase helpers ────────────────────────────────────────────────────

    /**
     * Offers the given weapon for sale.  If the player can afford it and
     * agrees, the purchase is made and the player's weapon is replaced.
     *
     * @param wepName the weapon on offer
     * @param scan    scanner for player input
     * @param user    the current player
     */
    public void canBuyWeapon(Weapon wepName, Scanner scan, Player user) {
        System.out.println("There is " + wepName.getName() + " its price is " + wepName.getPrice());
        System.out.println("You have " + user.getGold());
        if (user.getGold() >= wepName.getPrice()) {
            System.out.println("Do you want to buy this weapon? " + wepName.getName() + " y/n");
            System.out.println("Damage die: " + wepName.getDiceCount() + "d" + wepName.getDiceType() + "+ " + wepName.getBonus());
            System.out.println("Compare to yours: " + user.getWeapon() + " " + user.getBonus());
            String input1 = scan.next();
            if (input1.equals("y") || input1.equals("yes")) {
                user.setGold(user.getGold() - wepName.getPrice());
                System.out.println("You bought " + wepName.getName());
                user.setAttack(0);
                user.setWeapon(wepName);
                user.setAttack(user.dmg());
            } else {
                System.out.println("You didn't buy it!");
            }
        } else {
            System.out.println("Insufficient gold");
        }
    }

    /**
     * Offers the given potion for sale.  If the player can afford it and
     * agrees, the potion is added to the player's bag.
     *
     * @param potion the potion on offer
     * @param scan   scanner for player input
     * @param user   the current player
     */
    public void canBuyItem(Item potion, Scanner scan, Player user) {
        System.out.println("There is " + potion.getName() + " its price is " + potion.getPrice());
        System.out.println("You have " + user.getGold());
        if (user.getGold() >= potion.getPrice()) {
            System.out.println("Do you want to buy this potion? " + potion.getName() + " y/n");
            System.out.println("Heal for: " + potion.getHeal());
            String input1 = scan.next();
            if (input1.equals("y") || input1.equals("yes")) {
                user.setGold(user.getGold() - potion.getPrice());
                System.out.println("You bought " + potion.getName());
                user.getBag().put(potion.getName(), user.getBag().getOrDefault(potion.getName(), 0) + 1);
            } else {
                System.out.println("You didn't buy it!");
            }
        } else {
            System.out.println("Insufficient gold");
        }
    }

    /**
     * Offers the given armour for sale.  If the player can afford it and
     * agrees, the player's armour and AC are updated accordingly.
     *
     * @param armName the armour on offer
     * @param scan    scanner for player input
     * @param user    the current player
     */
    public void canBuyArmor(Armor armName, Scanner scan, Player user) {
        if (this.arm.getAc() == 0) {
            this.arm = new Armor();
        }
        System.out.println("There is " + armName.getName() + " its price is "
                + Acolor.YELLOW.get() + armName.getPrice() + Acolor.RESET.get());
        System.out.println("You have " + Acolor.YELLOW.get() + user.getGold() + Acolor.RESET.get());
        if (user.getGold() >= armName.getPrice()) {
            System.out.println("Do you want to buy this armor? " + armName.getName() + " y/n");
            System.out.println("Armor class: " + armName.getAc());
            String input1 = scan.next();
            if (input1.equals("y") || input1.equals("yes")) {
                user.setGold(user.getGold() - armName.getPrice());
                System.out.println("You bought " + armName.getName());
                user.setAc(user.getAc() - user.getArmor().getAc());
                user.setArmor(armName);
                user.setAc(user.getAc() + armName.getAc());
            } else {
                System.out.println("You didn't buy it!");
            }
        }
    }
}
